package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.domain.Role;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.RoleRepository;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RolesController {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;

    public RolesController(RoleRepository roleRepo, UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(roleRepo.findAll());
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assign(@RequestParam Long userId, @RequestParam String role) {
        User u = userRepo.findById(userId).orElseThrow();
        Role r = roleRepo.findByName(role).orElseThrow();
        u.getRoles().add(r);
        userRepo.save(u);
        return ResponseEntity.ok().build();
    }
}