package com.technorth.fluxivamed.core.medico.dto;

/**
 * DTO para representar um médico disponível, contendo apenas informações públicas.
 */
public record MedicoDisponivelDTO(
        Long id,
        String nomeCompleto,
        String crm,
        String especialidade
) {
}