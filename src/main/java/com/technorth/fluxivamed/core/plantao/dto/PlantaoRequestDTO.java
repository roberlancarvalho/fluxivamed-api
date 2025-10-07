package com.technorth.fluxivamed.core.plantao.dto;

import java.time.LocalDateTime;

public record PlantaoRequestDTO(
        Long hospitalId,
        LocalDateTime inicio,
        LocalDateTime fim,
        Double valor
) {}