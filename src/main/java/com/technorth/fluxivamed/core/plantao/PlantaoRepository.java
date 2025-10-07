package com.technorth.fluxivamed.core.plantao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlantaoRepository extends JpaRepository<Plantao, Long> {

    // --- MÉTODOS DE BUSCA DINÂMICA (SUBSTITUI A @Query COMPLEXA) ---
    // O Spring Data JPA cria a query sozinho baseado no nome do método!

    // Busca apenas por status, com paginação
    Page<Plantao> findByStatus(StatusPlantao status, Pageable pageable);

    // Busca por status E ID do hospital, com paginação
    Page<Plantao> findByStatusAndHospitalId(StatusPlantao status, Long hospitalId, Pageable pageable);

    // Busca por status E um intervalo de datas, com paginação
    Page<Plantao> findByStatusAndInicioBetween(StatusPlantao status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    // Busca por TODOS os filtros: status, ID do hospital E intervalo de datas, com paginação
    Page<Plantao> findByStatusAndHospitalIdAndInicioBetween(StatusPlantao status, Long hospitalId, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    /**
     * Busca todos os plantões (de qualquer status) associados a um médico específico,
     * identificando o médico pelo email do seu usuário de login.
     */
    List<Plantao> findByMedico_User_Email(String email);
}