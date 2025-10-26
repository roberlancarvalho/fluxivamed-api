package com.technorth.fluxivamed.core.dashboard;

import com.technorth.fluxivamed.core.dashboard.dto.AdminStatsResponseDTO;
import com.technorth.fluxivamed.core.dashboard.dto.MedicoStatsResponseDTO;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.plantao.PlantaoRepository;
import com.technorth.fluxivamed.core.candidatura.CandidaturaRepository;
import com.technorth.fluxivamed.core.pagamento.PagamentoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final MedicoRepository medicoRepository;
    private final PlantaoRepository plantaoRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final PagamentoRepository pagamentoRepository;

    public DashboardService(
            MedicoRepository medicoRepository,
            PlantaoRepository plantaoRepository,
            CandidaturaRepository candidaturaRepository,
            PagamentoRepository pagamentoRepository) {
        this.medicoRepository = medicoRepository;
        this.plantaoRepository = plantaoRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public AdminStatsResponseDTO getAdminStats() {
        LocalDateTime now = LocalDateTime.now();

        Long totalMedicos = medicoRepository.count();
        Long plantoesDisponiveis = plantaoRepository.countPlantoesDisponiveis(now);
        Long plantoesPendentes = candidaturaRepository.countCandidaturasPendentesGeral(now); // Contagem geral de candidaturas PENDENTES
        BigDecimal faturamentoPrevisto = plantaoRepository.sumFaturamentoPrevisto();

        return new AdminStatsResponseDTO(plantoesDisponiveis, plantoesPendentes, totalMedicos, faturamentoPrevisto);
    }

    public MedicoStatsResponseDTO getMedicoStats(Principal principal) {
        String medicoEmail = principal.getName();
        LocalDateTime now = LocalDateTime.now();

        Long proximosPlantoes = plantaoRepository.countProximosPlantoesDoMedico(medicoEmail, now);
        Long candidaturasPendentes = candidaturaRepository.countCandidaturasPendentesDoMedico(medicoEmail, now);
        BigDecimal pagamentosPendentes = pagamentoRepository.sumPagamentosPendentesDoMedico(medicoEmail);

        return new MedicoStatsResponseDTO(proximosPlantoes, candidaturasPendentes, pagamentosPendentes);
    }
}