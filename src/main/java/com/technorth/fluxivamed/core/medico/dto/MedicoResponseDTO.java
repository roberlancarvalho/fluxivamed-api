package com.technorth.fluxivamed.core.medico.dto;

public record MedicoResponseDTO(
        Long id,
        String crm,
        String nomeCompleto,
        String email,
        String telefone,
        String especialidadeNome
) {
}