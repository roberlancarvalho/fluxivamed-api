package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.medico.dto.MedicoDisponibilidadeRequest;
import com.technorth.fluxivamed.core.medico.dto.MedicoDisponivelDTO;
import com.technorth.fluxivamed.core.medico.dto.PeriodoDisponibilidadeResponseDTO;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medicos")
public class MedicoController {

    private final MedicoService medicoService;
    private final UserRepository userRepository;

    public MedicoController(MedicoService medicoService, UserRepository userRepository) {
        this.medicoService = medicoService;
        this.userRepository = userRepository;
    }

    // Endpoint para buscar médicos disponíveis (ESCALISTAS)
    @GetMapping("/disponibilidade")
    @PreAuthorize("hasAnyRole('HOSPITAL_ADMIN', 'ESCALISTA', 'MEDICO')")
    public ResponseEntity<List<MedicoDisponivelDTO>> getMedicosDisponiveis(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim, @RequestParam(required = false) String especialidade) {
        List<MedicoDisponivelDTO> medicos = medicoService.findDisponiveis(inicio, fim, especialidade);
        return ResponseEntity.ok(medicos);
    }

    // Endpoint para o médico logado buscar SUA disponibilidade
    @GetMapping("/minha-disponibilidade")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<PeriodoDisponibilidadeResponseDTO>> getMinhaDisponibilidade(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));
        Long medicoId = user.getId();

        List<PeriodoDisponibilidadeResponseDTO> disponibilidades = medicoService.getDisponibilidade(medicoId);
        return ResponseEntity.ok(disponibilidades);
    }

    // Endpoint para o médico logado definir SUA disponibilidade
    @PostMapping("/minha-disponibilidade")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Void> definirMinhaDisponibilidade(@Valid @RequestBody MedicoDisponibilidadeRequest request, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));
        Long medicoId = user.getId();

        medicoService.definirDisponibilidade(medicoId, request.getPeriodos());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/minha-disponibilidade/{id}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Void> removerMinhaDisponibilidade(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));
        Long medicoId = user.getId();

        medicoService.removerDisponibilidade(id, medicoId);
        return ResponseEntity.noContent().build();
    }
}