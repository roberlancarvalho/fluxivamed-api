package com.technorth.fluxivamed.core.medico.dto;

public record MedicoResponseDTO(
        Long id,
        String nomeCompleto,
        String crm,
        Long especialidadeId,
        String especialidadeNome,
        String email,
        String telefone
) {}