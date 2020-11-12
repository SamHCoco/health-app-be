package com.samhcoco.healthapp.core.service.impl;

import com.samhcoco.healthapp.core.model.SearchCriteria;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;;import java.util.List;


public class SpecificationBuilder<T> {

    private List<SearchCriteria> searchCriteria;

    private Specification<T> create(SearchCriteria criteria) {
        val field = criteria.getField();
        val operation = criteria.getOperation();
        val value = criteria.getValue();

        return (Specification<T>) (root, query, builder) -> {
            switch (operation) {
                case ">":
                    builder.greaterThan(root.get(field), value);
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
