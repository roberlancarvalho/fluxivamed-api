package com.technorth.fluxivamed.core.especialidade;

import com.technorth.fluxivamed.core.especialidade.dto.EspecialidadeRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/especialidades")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Especialidade>> listarEspecialidades() {
        List<Especialidade> especialidades = especialidadeService.listarTodas();
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<Especialidade> getEspecialidadeById(@PathVariable Long id) {
        Especialidade especialidade = especialidadeService.buscarPorId(id);
        return ResponseEntity.ok(especialidade);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<Especialidade> createEspecialidade(@Valid @RequestBody EspecialidadeRequestDTO dto) {
        Especialidade novaEspecialidade = especialidadeService.criarEspecialidade(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEspecialidade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<Especialidade> updateEspecialidade(@PathVariable Long id, @Valid @RequestBody EspecialidadeRequestDTO dto) {
        Especialidade especialidadeAtualizada = especialidadeService.atualizarEspecialidade(id, dto);
        return ResponseEntity.ok(especialidadeAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<?> deleteEspecialidade(@PathVariable Long id) {
        try {
            especialidadeService.excluirEspecialidade(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}