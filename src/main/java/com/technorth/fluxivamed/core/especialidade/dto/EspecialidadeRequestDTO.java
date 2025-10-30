package com.technorth.fluxivamed.core.especialidade.dto;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadeRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome
) {}