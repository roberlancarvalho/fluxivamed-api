package com.technorth.fluxivamed.core.especialidade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/especialidades")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN', 'ESCALISTA', 'MEDICO')")
    // ALTERAÇÃO 1: Mudar nome do método e tipo de retorno para List<Especialidade>
    public ResponseEntity<List<Especialidade>> listarEspecialidades() {
        // ALTERAÇÃO 2: Buscar a lista completa de objetos
        List<Especialidade> especialidades = especialidadeService.listarTodas();
        // ALTERAÇÃO 3: Retornar diretamente a lista de objetos
        return ResponseEntity.ok(especialidades);
    }
}