package com.technorth.fluxivamed.core.medico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoRequestDTO(
        @NotBlank
        String nomeCompleto,

        @NotBlank
        String crm,

        @NotNull
        Long especialidadeId,

        @NotBlank
        @Email
        String email,

        String telefone
) {}