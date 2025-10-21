package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.medico.dto.MedicoDisponivelDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // Endpoint para buscar médicos disponíveis para um plantão (uso de escalistas)
    @GetMapping("/disponibilidade") // Caminho original
    @PreAuthorize("hasAnyRole('HOSPITAL_ADMIN', 'ESCALISTA', 'MEDICO')")
    public ResponseEntity<List<MedicoDisponivelDTO>> getMedicosDisponiveis( // Renomeei o método para clareza
                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
                                                                            @RequestParam(required = false) String especialidade
    ) {
        List<MedicoDisponivelDTO> medicos = medicoService.findDisponiveis(inicio, fim, especialidade);
        return ResponseEntity.ok(medicos);
    }


}
