package com.technorth.fluxivamed.core.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Importe para valores monetários

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera construtor sem argumentos
@AllArgsConstructor // Gera construtor com todos os argumentos
public class AdminStatsResponseDTO {
    private Long plantoesDisponiveis;
    private Long plantoesPendentes;
    private Long totalMedicos;
    private BigDecimal faturamentoPrevisto; // Use BigDecimal para dinheiro
}