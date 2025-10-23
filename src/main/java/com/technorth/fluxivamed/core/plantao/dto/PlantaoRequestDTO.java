package com.technorth.fluxivamed.core.plantao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
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
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
        BigDecimal valor
) {}