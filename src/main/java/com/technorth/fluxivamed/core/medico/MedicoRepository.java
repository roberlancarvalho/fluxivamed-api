package com.technorth.fluxivamed.core.medico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Importe Optional para findByEmail

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Query("SELECT m FROM Medico m WHERE " +
            "(:especialidade IS NULL OR m.especialidade = :especialidade) AND " +
            "m.id NOT IN (" +
            "  SELECT p.medico.id FROM Plantao p WHERE p.medico IS NOT NULL AND " +
            "  (p.dataInicio < :fim AND p.dataFim > :inicio)" +
            ")")
    List<Medico> findMedicosDisponiveis(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("especialidade") String especialidade);

    Optional<Medico> findByUserEmail(String email);
    Optional<Medico> findByUserId(Long userId);
}