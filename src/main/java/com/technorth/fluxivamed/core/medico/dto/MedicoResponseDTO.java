package com.technorth.fluxivamed.core.medico.dto;

import com.technorth.fluxivamed.domain.User;

public record MedicoResponseDTO(
        Long id,
        String crm,
        String nomeCompleto,
        String email,
        String telefone,
        String especialidadeNome
) {}