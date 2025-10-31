package com.technorth.fluxivamed.core.plantao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaoRepository extends JpaRepository<Plantao, Long>, JpaSpecificationExecutor<Plantao> {

    @Query("SELECT p FROM Plantao p LEFT JOIN FETCH p.medico m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.hospital h WHERE m.user.email = :email")
    List<Plantao> findByMedico_User_Email(@Param("email") String email);

    @Query("SELECT p FROM Plantao p " +
            "LEFT JOIN FETCH p.candidatos c " +
            "LEFT JOIN FETCH c.medico candMedico " +     // <-- Correção 1: Junte o médico da candidatura (com novo alias 'candMedico')
            "LEFT JOIN FETCH candMedico.user " +         // <-- Correção 2: Junte o user a partir do 'candMedico'
            "LEFT JOIN FETCH p.medico m " +
            "LEFT JOIN FETCH m.user " +
            "LEFT JOIN FETCH p.hospital h " +
            "WHERE p.id = :id")
    Optional<Plantao> findByIdWithCandidatos(@Param("id") Long id);

    @Query("SELECT p FROM Plantao p LEFT JOIN FETCH p.medico m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.hospital h WHERE p.id = :id")
    Optional<Plantao> findById(@Param("id") Long id);

    // --- Métodos para o Dashboard ---

    // Conta plantões com status DISPONIVEL e data de início no futuro
    @Query("SELECT COUNT(p) FROM Plantao p WHERE p.status = 'DISPONIVEL' AND p.dataInicio > :now")
    long countPlantoesDisponiveis(@Param("now") LocalDateTime now);

    // Soma o valor de plantões com status que geram faturamento (PREENCHIDO, REALIZADO, CONCLUIDO_NAO_PAGO)
    // Ajuste os status conforme sua regra de "faturamento previsto"
    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Plantao p WHERE p.status IN ('PREENCHIDO', 'REALIZADO', 'CONCLUIDO_NAO_PAGO')")
    BigDecimal sumFaturamentoPrevisto();

    // Conta os plantões PREENCHIDOS (equivalente a AGENDADO/CONFIRMADO) de um médico específico no futuro
    // Se você tiver um status mais específico para "confirmado", ajuste aqui.
    @Query("SELECT COUNT(p) FROM Plantao p WHERE p.medico.user.email = :medicoEmail AND p.dataInicio > :now AND p.status = 'PREENCHIDO'")
    long countProximosPlantoesDoMedico(@Param("medicoEmail") String medicoEmail, @Param("now") LocalDateTime now);

    List<Plantao> findByMedicoId(Long medicoId);
}