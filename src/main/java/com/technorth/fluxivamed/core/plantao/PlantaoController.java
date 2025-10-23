package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.plantao.dto.PlantaoRequestDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<PlantaoResponseDTO> criarPlantao(@RequestBody PlantaoRequestDTO requestDTO) {
        PlantaoResponseDTO novoPlantao = plantaoService.criarPlantao(requestDTO);
        return ResponseEntity.status(201).body(novoPlantao);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Page<PlantaoResponseDTO>> buscarDisponiveis(@RequestParam(required = false) Long hospitalId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, Pageable pageable) {
        Page<PlantaoResponseDTO> plantoes = plantaoService.findAvailable(hospitalId, data, pageable);
        return ResponseEntity.ok(plantoes);
    }

    @PostMapping("/{plantaoId}/candidatar")
    public ResponseEntity<PlantaoResponseDTO> candidatarPlantao(@PathVariable Long plantaoId, Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String medicoEmail = authentication.getName();

        PlantaoResponseDTO plantaoAtualizado = plantaoService.candidatar(plantaoId, medicoEmail);
        return ResponseEntity.ok(plantaoAtualizado);
    }

    @PutMapping("/{plantaoId}/aprovar/{medicoId}")
    public ResponseEntity<PlantaoResponseDTO> aprovarCandidatura(@PathVariable Long plantaoId, @PathVariable Long medicoId) {
        PlantaoResponseDTO plantaoAtualizado = plantaoService.aprovarCandidatura(plantaoId, medicoId);
        return ResponseEntity.ok(plantaoAtualizado);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PlantaoResponseDTO>> buscarMeusPlantoes(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        List<PlantaoResponseDTO> meusPlantoes = plantaoService.findByMedico(email);
        return ResponseEntity.ok(meusPlantoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantaoResponseDTO> getPlantaoById(@PathVariable Long id) {
        PlantaoResponseDTO plantao = plantaoService.getPlantaoDtoById(id);
        return ResponseEntity.ok(plantao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantaoResponseDTO> atualizarPlantao(@PathVariable Long id, @RequestBody PlantaoRequestDTO plantaoRequestDTO) {
        PlantaoResponseDTO plantaoAtualizado = plantaoService.atualizarPlantao(id, plantaoRequestDTO);
        return ResponseEntity.ok(plantaoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPlantao(@PathVariable Long id) {
        plantaoService.excluirPlantao(id);
        return ResponseEntity.noContent().build();
    }
}