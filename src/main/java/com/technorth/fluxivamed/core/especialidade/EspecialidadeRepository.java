package com.technorth.fluxivamed.core.especialidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Adicionar import
import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    Optional<Especialidade> findByNome(String nome);
    boolean existsByNome(String nome);
}