package com.technorth.fluxivamed.auth.dto;

public record ProfileResponseDTO(
        Long id,
        String email,
        String fullName,
        String telefone,
        String crm,
        Long especialidadeId,
        String especialidadeNome,
        String fotoUrl
) {}