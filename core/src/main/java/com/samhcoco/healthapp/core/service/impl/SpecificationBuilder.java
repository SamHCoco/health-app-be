package com.samhcoco.healthapp.core.service.impl;

import com.samhcoco.healthapp.core.model.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;;import java.util.List;


@AllArgsConstructor
public class SpecificationBuilder<T> {

    private List<SearchCriteria> searchCriteria;

    public Specification<T> build() {
        if (searchCriteria.isEmpty()) {
            return null;
        }
        val specification = create(searchCriteria.remove(0));
        searchCriteria.forEach(criteria -> {
            specification.and(create(criteria));
        });
        return specification;
    }

    private Specification<T> create(@NonNull SearchCriteria criteria) {
        val field = criteria.getField();
        val operation = criteria.getOperation();
        val value = criteria.getValue();

        return (Specification<T>) (root, query, builder) -> {
            switch (operation) {
                case ">":
                    return builder.greaterThan(root.get(field), value);
                case ">=":
                    return builder.greaterThanOrEqualTo(root.get(field), value);
                case "==":
                    return builder.equal(root.get(field), value);
                case "<=":
                    return builder.lessThanOrEqualTo(root.get(field), value);
                case "<":
                    return builder.lessThan(root.get(field), value);
                case "like":
                    return builder.like(root.get(field), "%" + value + "%s");
                default:
                    return null;
            }
        };
    }


}
