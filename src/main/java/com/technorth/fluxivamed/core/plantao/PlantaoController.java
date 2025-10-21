package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.plantao.dto.PlantaoRequestDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plantoes")
public class PlantaoController {

    private final PlantaoService plantaoService;

    public PlantaoController(PlantaoService plantaoService) {
        this.plantaoService = plantaoService;
    }

    /**
     * Feature 1: Endpoint de busca com filtros e paginação.
     * Exemplo de chamada: GET /api/v1/plantoes/disponiveis?hospitalId=1&data=2025-10-21&page=0&size=10
     */
    @GetMapping("/disponiveis")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Page<PlantaoResponseDTO>> buscarDisponiveis(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Pageable pageable) {
        return ResponseEntity.ok(plantaoService.findAvailable(hospitalId, data, pageable));
    }

    /**
     * Feature 2: Endpoint para um médico se candidatar a um plantão.
     */

    @PostMapping("/{plantaoId}/candidatar-se")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<PlantaoResponseDTO> candidatarSeAoPlantao(
            @PathVariable Long plantaoId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String medicoEmail = authentication.getName();

        PlantaoResponseDTO dto = plantaoService.candidatar(plantaoId, medicoEmail);
        return ResponseEntity.ok(dto);
    }

    /**
     * Feature 3: Endpoint para o médico ver sua própria agenda de plantões.
     */
    @GetMapping("/meus-plantoes")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<PlantaoResponseDTO>> getMeusPlantoes(Authentication authentication) {
        String medicoEmail = authentication.getName();
        List<PlantaoResponseDTO> meusPlantoes = plantaoService.findByMedico(medicoEmail);
        return ResponseEntity.ok(meusPlantoes);
    }

    /**
     * Feature 4: Endpoint para o admin aprovar médido ao plantão.
     */
    @PostMapping("/{plantaoId}/aprovar/{medicoId}")
    @PreAuthorize("hasRole('HOSPITAL_ADMIN')")
    public ResponseEntity<PlantaoResponseDTO> aprovarCandidato(
            @PathVariable Long plantaoId,
            @PathVariable Long medicoId) {

        PlantaoResponseDTO dto = plantaoService.aprovarCandidatura(plantaoId, medicoId) ;
        return ResponseEntity.ok(dto);
    }

    /**
     * Feature 5: Endpoint para criar um novo plantão.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL_ADMIN', 'ESCALISTA')")
    public ResponseEntity<PlantaoResponseDTO> criarNovoPlantao(@Valid @RequestBody PlantaoRequestDTO requestDTO) {
        PlantaoResponseDTO plantaoCriado = plantaoService.criarPlantao(requestDTO);
        return new ResponseEntity<>(plantaoCriado, HttpStatus.CREATED);
    }
}