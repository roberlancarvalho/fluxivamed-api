package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.domain.Role;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.RoleRepository;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final RoleRepository roles;
    private final MedicoRepository medicos;

    public AuthController(AuthenticationManager am, JwtEncoder en, PasswordEncoder pe,
                          UserRepository ur, RoleRepository rr, MedicoRepository mr) {
        this.authManager = am;
        this.encoder = en;
        this.passwordEncoder = pe;
        this.users = ur;
        this.roles = rr;
        this.medicos = mr;
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            String fullName,
            String tenantId,
            String role,
            String crm,
            String especialidade) {}

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record TokenResponse(String accessToken, long expiresIn) {}

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
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

        if (strRole.equals("MEDICO")) {
            if (req.crm() == null || req.especialidade() == null) {
                throw new IllegalArgumentException("CRM e Especialidade são obrigatórios para o perfil MEDICO.");
            }
            Medico medicoProfile = new Medico();
            medicoProfile.setUser(savedUser);
            medicoProfile.setCrm(req.crm());
            medicoProfile.setEspecialidade(req.especialidade());
            medicos.save(medicoProfile);
        }

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        var user = users.findByEmail(req.email()).orElseThrow();
        var now = Instant.now();
        long expiry = 3600;

        var scope = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

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
        String token = encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();

        return new TokenResponse(token, expiry);
    }
}