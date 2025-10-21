package com.technorth.fluxivamed.core.medico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /**
     * Encontra médicos disponíveis que não possuem plantões conflitantes com o intervalo de tempo fornecido.
     * Um conflito ocorre se um plantão existente começa antes do fim do novo plantão E
     * termina depois do início do novo plantão.
     *
     * @param inicio O início do intervalo de disponibilidade desejado.
     * @param fim O fim do intervalo de disponibilidade desejado.
     * @param especialidade A especialidade desejada (opcional).
     * @return Uma lista de médicos disponíveis.
     */
    @Query("SELECT m FROM Medico m WHERE " +
            "(:especialidade IS NULL OR m.especialidade = :especialidade) AND " +
            "m.id NOT IN (" +
            "  SELECT p.medico.id FROM Plantao p WHERE p.medico IS NOT NULL AND " +
            "  (p.inicio < :fim AND p.fim > :inicio)" +
            ")")
    List<Medico> findMedicosDisponiveis(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("especialidade") String especialidade);
}