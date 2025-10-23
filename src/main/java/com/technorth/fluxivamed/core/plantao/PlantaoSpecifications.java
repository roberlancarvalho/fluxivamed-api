package com.technorth.fluxivamed.core.plantao;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PlantaoSpecifications {

    public static Specification<Plantao> withFilters(StatusPlantao status, Long hospitalId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return (root, query, cb) -> {
            // Para N+1 problem, se necessário, adicione joins aqui para otimizar:
            // Isso evita N+1 problem ao buscar a lista, garantindo que medico, user e hospital
            // são carregados com a query principal, similar ao seu FETCH na query JPQL.
            if (Long.class != query.getResultType()) { // Evita erro se a query for de count
                root.fetch("medico", JoinType.LEFT).fetch("user", JoinType.LEFT);
                root.fetch("hospital", JoinType.LEFT);
            }

            jakarta.persistence.criteria.Predicate predicate = cb.conjunction(); // Inicia com um "true" para encadear ANDs

            // Condição de status (sempre presente)
            predicate = cb.and(predicate, cb.equal(root.get("status"), status));

            // Condição de hospitalId
            if (hospitalId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("hospital").get("id"), hospitalId));
            }

            // Condição de dataInicio
            if (dataInicio != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("inicio"), dataInicio));
            }

            // Condição de dataFim
            if (dataFim != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("fim"), dataFim));
            }

            return predicate;
        };
    }
}