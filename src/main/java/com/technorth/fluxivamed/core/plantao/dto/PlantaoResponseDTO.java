package com.technorth.fluxivamed.core.plantao.dto;

import com.technorth.fluxivamed.core.plantao.StatusPlantao;
import java.time.LocalDateTime;

public record PlantaoResponseDTO(
        Long id,
        Long hospitalId,
        String nomeHospital,
        Long medicoId,
        String nomeMedico,
        String especialidade,
        LocalDateTime inicio,
        LocalDateTime fim,
        Double valor,
        StatusPlantao status
) {}