package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.domain.Role;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.RoleRepository;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final RoleRepository roles;

    private static final long EXPIRY_SECONDS = 3600;

    public AuthService(AuthenticationManager am, JwtEncoder enc, PasswordEncoder pe,
                       UserRepository ur, RoleRepository rr) {
        this.authManager = am;
        this.encoder = enc;
        this.passwordEncoder = pe;
        this.users = ur;
        this.roles = rr;
    }

    public void register(String email, String rawPassword, String fullName, String tenantId) {
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setFullName(fullName);                  // <-- aqui é fullName (não name)
        u.setTenantId(tenantId);

        // role padrão MEDICO
        Role defaultRole = roles.findByName("MEDICO")
                .orElseGet(() -> roles.save(new Role("MEDICO")));
        u.setRoles(Set.of(defaultRole));

        users.save(u);
    }

    public Token login(String email, String rawPassword) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );
        var user = users.findByEmail(email).orElseThrow();

        var now = Instant.now();
        var expiry = EXPIRY_SECONDS;
        var scope = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName()) // alinhado ao que você já usa
                .toList();

        var claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .claim("scope", scope)          // você já está usando 'scope'
                .claim("tenant", user.getTenantId())
                .build();

        var token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new Token(token, expiry);
    }

    public record Token(String accessToken, long expiresIn) {}
}
