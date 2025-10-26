package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.core.especialidade.Especialidade; // Adicionar este import
import com.technorth.fluxivamed.core.especialidade.EspecialidadeRepository; // Adicionar este import
import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.domain.Role;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.RoleRepository;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final RoleRepository roles;
    private final MedicoRepository medicos;
    private final EspecialidadeRepository especialidades;

    public AuthService(AuthenticationManager am, JwtEncoder enc, PasswordEncoder pe,
                       UserRepository ur, RoleRepository rr, MedicoRepository mr,
                       EspecialidadeRepository er) {
        this.authManager = am;
        this.encoder = enc;
        this.passwordEncoder = pe;
        this.users = ur;
        this.roles = rr;
        this.medicos = mr;
        this.especialidades = er;
    }

    @Transactional
    public void register(AuthController.RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setTenantId(req.tenantId());

        String strRole = (req.role() == null || req.role().isBlank()) ? "MEDICO" : req.role().toUpperCase();

        Role userRole = roles.findByName(strRole)
                .orElseThrow(() -> new RuntimeException("Error: Role '" + strRole + "' is not found."));

        user.setRoles(Collections.singleton(userRole));
        User savedUser = users.save(user);

        if ("MEDICO".equals(strRole)) {
            if (req.crm() == null || req.especialidade() == null) {
                throw new IllegalArgumentException("CRM e Especialidade são obrigatórios para o perfil MEDICO.");
            }
            Medico medicoProfile = new Medico();
            medicoProfile.setUser(savedUser);
            medicoProfile.setCrm(req.crm());

            Especialidade especialidadeObj = especialidades.findByNome(req.especialidade())
                    .orElseGet(() -> {
                        Especialidade novaEspecialidade = new Especialidade(req.especialidade());
                        return especialidades.save(novaEspecialidade);
                    });

            medicoProfile.setEspecialidade(especialidadeObj);
            medicos.save(medicoProfile);
        }
    }

    public Token login(String email, String rawPassword) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));

        var user = users.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found after authentication"));
        var now = Instant.now();
        long expiry = 3600;

        var scope = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        var claimsBuilder = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .claim("scope", scope)
                .claim("fullName", user.getFullName());

        if (user.getTenantId() != null && !user.getTenantId().isBlank()) {
            claimsBuilder.claim("tenant", user.getTenantId());
        }

        var claims = claimsBuilder.build();
        var headers = JwsHeader.with(MacAlgorithm.HS256).build();
        String tokenValue = encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();

        return new Token(tokenValue, expiry);
    }

    public record Token(String accessToken, long expiresIn) {}
}