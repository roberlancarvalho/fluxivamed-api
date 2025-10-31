package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.auth.dto.ProfileResponseDTO;
import com.technorth.fluxivamed.auth.dto.ProfileUpdateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        ProfileResponseDTO profile = profileService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponseDTO> updateMyProfile(Authentication authentication, @Valid @RequestBody ProfileUpdateRequestDTO dto) {
        String email = authentication.getName();
        ProfileResponseDTO updatedProfile = profileService.updateProfile(email, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            String email = authentication.getName();
            String fotoUrl = profileService.atualizarFotoPerfil(email, file);
            return ResponseEntity.ok(Map.of("fotoUrl", fotoUrl));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Falha ao processar o upload do arquivo."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(Authentication authentication) {
        String email = authentication.getName();
        profileService.deleteProfile(email);
        return ResponseEntity.noContent().build();
    }
}