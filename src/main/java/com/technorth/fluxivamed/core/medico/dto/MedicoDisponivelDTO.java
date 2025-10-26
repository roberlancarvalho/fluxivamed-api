package com.technorth.fluxivamed.core.medico.dto;

public record MedicoDisponivelDTO(
        Long id,
        String nomeCompleto,
        String crm,
        com.technorth.fluxivamed.core.especialidade.Especialidade especialidade
) {
}