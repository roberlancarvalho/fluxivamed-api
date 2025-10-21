package com.technorth.fluxivamed.core.medico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {

    // Método para buscar todas as disponibilidades de um médico específico
    List<Disponibilidade> findByMedicoId(Long medicoId);

    // Método para deletar todas as disponibilidades de um médico (para a estratégia de substituição)
    void deleteByMedicoId(Long medicoId);
}