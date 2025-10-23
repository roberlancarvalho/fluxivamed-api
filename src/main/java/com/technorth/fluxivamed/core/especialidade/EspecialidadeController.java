package com.technorth.fluxivamed.core.especialidade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/especialidades")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN', 'ESCALISTA', 'MEDICO')")
    public ResponseEntity<List<String>> listarNomesEspecialidades() {
        List<Especialidade> especialidades = especialidadeService.listarTodas();
        List<String> nomes = especialidades.stream()
                .map(Especialidade::getNome)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nomes);
    }
}