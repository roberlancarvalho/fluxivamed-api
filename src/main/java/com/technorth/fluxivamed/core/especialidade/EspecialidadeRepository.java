package com.technorth.fluxivamed.core.especialidade;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    Optional<Especialidade> findByNome(String nome);
    boolean existsByNome(String nome);
}