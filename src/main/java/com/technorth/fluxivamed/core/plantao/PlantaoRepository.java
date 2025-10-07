package com.technorth.fluxivamed.core.plantao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaoRepository extends JpaRepository<Plantao, Long> {

    List<Plantao> findByMedico_User_Email(String email);

    @Query("SELECT p FROM Plantao p LEFT JOIN FETCH p.candidatos WHERE p.id = :id")
    Optional<Plantao> findByIdWithCandidatos(@Param("id") Long id);

    @Query("SELECT p FROM Plantao p WHERE p.status = :status " +
            "AND (:hospitalId IS NULL OR p.hospital.id = :hospitalId) " +
            "AND (CAST(:dataInicio AS timestamp) IS NULL OR p.inicio >= :dataInicio) " +
            "AND (CAST(:dataFim AS timestamp) IS NULL OR p.inicio < :dataFim)")
    Page<Plantao> findAvailableWithFilters(
            @Param("status") StatusPlantao status,
            @Param("hospitalId") Long hospitalId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);
}