package com.technorth.fluxivamed.core.hospital;

import com.technorth.fluxivamed.core.hospital.dto.HospitalRequestDTO;
import com.technorth.fluxivamed.core.hospital.dto.HospitalResponseDTO;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional(readOnly = true)
    public HospitalResponseDTO findHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Hospital não encontrado com ID: " + id));
    }

    @Transactional
    public HospitalResponseDTO createHospital(HospitalRequestDTO dto) {
        Hospital hospital = new Hospital();
        hospital.setNome(dto.nome());
        hospital.setCnpj(dto.cnpj());
        hospital.setEndereco(dto.endereco());
        hospital.setTelefone1(dto.telefone1());
        hospital.setTelefone2(dto.telefone2());

        Hospital hospitalSalvo = hospitalRepository.save(hospital);
        return convertToDto(hospitalSalvo);
    }

    @Transactional
    public HospitalResponseDTO updateHospital(Long id, HospitalRequestDTO dto) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hospital não encontrado com ID: " + id));

        hospital.setNome(dto.nome());
        hospital.setCnpj(dto.cnpj());
        hospital.setEndereco(dto.endereco());
        hospital.setTelefone1(dto.telefone1());
        hospital.setTelefone2(dto.telefone2());

        Hospital hospitalAtualizado = hospitalRepository.save(hospital);
        return convertToDto(hospitalAtualizado);
    }

    @Transactional
    public void deleteHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new EntityNotFoundException("Hospital não encontrado com ID: " + id);
        }
        hospitalRepository.deleteById(id);
    }

    private HospitalResponseDTO convertToDto(Hospital hospital) {
        return new HospitalResponseDTO(
                hospital.getId(),
                hospital.getNome(),
                hospital.getCnpj(),
                hospital.getEndereco(),
                hospital.getTelefone1(),
                hospital.getTelefone2()
        );
    }
}