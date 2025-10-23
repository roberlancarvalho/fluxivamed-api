package com.technorth.fluxivamed.core.plantao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaoRepository extends JpaRepository<Plantao, Long>, JpaSpecificationExecutor<Plantao> {

    // Consulta para buscar plantões de um médico, carregando o médico e o hospital
    @Query("SELECT p FROM Plantao p LEFT JOIN FETCH p.medico m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.hospital h WHERE m.user.email = :email")
    List<Plantao> findByMedico_User_Email(@Param("email") String email);

    // Consulta para buscar um plantão por ID e carregar seus candidatos, médico associado e hospital
    @Query("SELECT p FROM Plantao p " +
            "LEFT JOIN FETCH p.candidatos c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.medico m LEFT JOIN FETCH m.user " +
            "LEFT JOIN FETCH p.hospital h " +
            "WHERE p.id = :id")
    Optional<Plantao> findByIdWithCandidatos(@Param("id") Long id);

    // Sobrescrevendo findById padrão para sempre carregar o médico e seus dados
    // (Esta query é para quando você simplesmente chama findById(id) e precisa do médico/hospital)
    @Query("SELECT p FROM Plantao p LEFT JOIN FETCH p.medico m LEFT JOIN FETCH m.user LEFT JOIN FETCH p.hospital h WHERE p.id = :id")
    Optional<Plantao> findById(@Param("id") Long id);
}