package com.technorth.fluxivamed.core.hospital;

import com.technorth.fluxivamed.core.hospital.dto.HospitalRequestDTO;
import com.technorth.fluxivamed.core.hospital.dto.HospitalResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospitais")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HospitalResponseDTO>> getAllHospitais() {
        List<HospitalResponseDTO> hospitais = hospitalService.findAllHospitais();
        return ResponseEntity.ok(hospitais);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN', 'ESCALISTA')")
    public ResponseEntity<HospitalResponseDTO> getHospitalById(@PathVariable Long id) {
        HospitalResponseDTO hospital = hospitalService.findHospitalById(id);
        return ResponseEntity.ok(hospital);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<HospitalResponseDTO> createHospital(@Valid @RequestBody HospitalRequestDTO dto) {
        HospitalResponseDTO novoHospital = hospitalService.createHospital(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoHospital);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<HospitalResponseDTO> updateHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequestDTO dto) {
        HospitalResponseDTO hospitalAtualizado = hospitalService.updateHospital(id, dto);
        return ResponseEntity.ok(hospitalAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
}