package com.technorth.fluxivamed.core.plantao;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlantaoSpecifications {

    public static Specification<Plantao> withFilters(
            List<StatusPlantao> statuses,
            Long hospitalId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        return (root, query, cb) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("hospital", JoinType.LEFT);
                root.fetch("especialidade", JoinType.LEFT);
                root.fetch("medico", JoinType.LEFT).fetch("user", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (hospitalId != null) {
                predicates.add(cb.equal(root.get("hospital").get("id"), hospitalId));
            }

            if (dataInicio != null && dataFim != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dataInicio));
                predicates.add(cb.lessThan(root.get("dataInicio"), dataFim));
            } else if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dataInicio));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}