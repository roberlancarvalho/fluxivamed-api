package com.technorth.fluxivamed.core.hospital.dto;

public record HospitalResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String endereco,
        String telefone1,
        String telefone2
) {}