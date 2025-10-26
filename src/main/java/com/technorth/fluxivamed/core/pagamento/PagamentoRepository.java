package com.technorth.fluxivamed.core.pagamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // Soma os pagamentos com status PENDENTE para um médico específico
    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p WHERE p.medico.user.email = :medicoEmail AND p.status = 'PENDENTE'")
    BigDecimal sumPagamentosPendentesDoMedico(@Param("medicoEmail") String medicoEmail);
}