package com.technorth.fluxivamed.core.plantao.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record PlantaoRequestDTO(
        @NotNull
        Long hospitalId,

        @NotNull
        String especialidade,

        @NotNull
        @Future(message = "A data de início deve ser no futuro.")
        LocalDateTime inicio,

        @NotNull
        @Future(message = "A data de fim deve ser no futuro.")
        LocalDateTime fim,

        @NotNull
        @Positive(message = "O valor deve ser positivo.")
        Double valor
) {}
