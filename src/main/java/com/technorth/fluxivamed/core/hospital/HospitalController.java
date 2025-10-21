package com.technorth.fluxivamed.core.hospital;

import com.technorth.fluxivamed.core.hospital.dto.HospitalResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospitais")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * Endpoint para buscar uma lista de todos os hospitais.
     * Usado no frontend para popular dropdowns de seleção ao criar plantões.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Permite que qualquer usuário autenticado (médico, admin, etc.) veja a lista.
    public ResponseEntity<List<HospitalResponseDTO>> getAllHospitais() {
        // Corrigido: O método que criamos no HospitalService chama-se 'findAllHospitais'
        List<HospitalResponseDTO> hospitais = hospitalService.findAllHospitais();
        return ResponseEntity.ok(hospitais);
    }
}