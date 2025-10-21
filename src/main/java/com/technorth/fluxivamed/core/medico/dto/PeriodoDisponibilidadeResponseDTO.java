package com.technorth.fluxivamed.core.medico.dto;

import java.time.LocalDateTime;

public record PeriodoDisponibilidadeResponseDTO(
        Long id,
        LocalDateTime inicio,
        LocalDateTime fim
) {}