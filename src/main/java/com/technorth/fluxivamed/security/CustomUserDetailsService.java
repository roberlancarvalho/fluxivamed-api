package com.technorth.fluxivamed.security;

import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = users.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String[] authorities = u.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .toArray(String[]::new);
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(u.getEnabled()))
                .build();
    }
}
