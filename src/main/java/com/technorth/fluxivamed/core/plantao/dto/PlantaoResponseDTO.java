package com.technorth.fluxivamed.core.plantao.dto;

import com.technorth.fluxivamed.core.medico.dto.MedicoResponseDTO;
import com.technorth.fluxivamed.core.plantao.StatusPlantao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PlantaoResponseDTO(
        Long id,
        Long hospitalId,
        String nomeHospital,
        Long medicoId,
        String nomeMedico,
        String especialidade,
        LocalDateTime inicio,
        LocalDateTime fim,
        BigDecimal valor,
        StatusPlantao status,
        List<MedicoResponseDTO> candidatos
) {}