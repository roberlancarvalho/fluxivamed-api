package com.technorth.fluxivamed.core.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicoStatsResponseDTO {
    private Long proximosPlantoes;
    private Long candidaturasPendentes;
    private BigDecimal pagamentosPendentes;
}