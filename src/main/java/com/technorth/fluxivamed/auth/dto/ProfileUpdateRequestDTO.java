package com.technorth.fluxivamed.auth.dto;

import com.technorth.fluxivamed.core.especialidade.Especialidade;
import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequestDTO(
        @NotBlank String fullName,
        String telefone,
        String password,
        String crm,
        Especialidade especialidade
) {}