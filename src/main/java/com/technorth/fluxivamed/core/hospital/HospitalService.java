package com.technorth.fluxivamed.core.hospital;

import com.technorth.fluxivamed.core.hospital.dto.HospitalResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Transactional(readOnly = true)
    public List<HospitalResponseDTO> findAllHospitais() {
        return hospitalRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HospitalResponseDTO convertToDto(Hospital hospital) {
        return new HospitalResponseDTO(
                hospital.getId(),
                hospital.getNome(),
                hospital.getCnpj(),
                hospital.getEndereco()
        );
    }

    // (Aqui entrarão os futuros métodos de POST, PUT, DELETE)
}