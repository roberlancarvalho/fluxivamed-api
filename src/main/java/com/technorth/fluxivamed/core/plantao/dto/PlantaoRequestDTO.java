package com.technorth.fluxivamed.core.plantao.dto;

import java.time.LocalDateTime;

// Usando Record para um DTO imutável e conciso
public record PlantaoRequestDTO(
        Long hospitalId,
        LocalDateTime inicio,
        LocalDateTime fim,
        Double valor
) {}