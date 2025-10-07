package com.technorth.fluxivamed; // MUDANÇA: Pacote base do projeto

import com.technorth.fluxivamed.domain.Role;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    public record UserMe(Long id, String fullName, String email, java.util.List<String> roles) {}

    @GetMapping("/me")
    public UserMe me(Authentication auth) {
        // O auth.getName() agora retorna o email, como definido no UserDetails
        User u = users.findByEmail(auth.getName()).orElseThrow();
        var roles = u.getRoles().stream().map(Role::getName).toList();
        return new UserMe(u.getId(), u.getFullName(), u.getEmail(), roles);
    }
}