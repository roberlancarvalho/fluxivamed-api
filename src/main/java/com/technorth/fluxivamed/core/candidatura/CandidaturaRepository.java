package com.technorth.fluxivamed.core.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    // Conta todas as candidaturas que estão PENDENTE e referem-se a plantões futuros (para ADMIN/ESCALISTA)
    @Query("SELECT COUNT(c) FROM Candidatura c WHERE c.status = 'PENDENTE' AND c.plantao.dataInicio > :now")
    long countCandidaturasPendentesGeral(@Param("now") LocalDateTime now);

    // Conta as candidaturas PENDENTE de um médico específico para plantões futuros
    @Query("SELECT COUNT(c) FROM Candidatura c WHERE c.medico.user.email = :medicoEmail AND c.status = 'PENDENTE' AND c.plantao.dataInicio > :now")
    long countCandidaturasPendentesDoMedico(@Param("medicoEmail") String medicoEmail, @Param("now") LocalDateTime now);
}