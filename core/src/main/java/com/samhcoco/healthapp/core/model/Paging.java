package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paging {

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;

    public PageRequest buildRequest() {
        if (isNull(page) || page < 0) {
            page = 0;
        }
        if (isNull(size) || size <= 0) {
            size = 50;
        }

        val validDirections = Set.of("ASC", "DESC");
        if (!validDirections.contains(sortBy)) {
            direction = "ASC";
        }
        return PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortBy);
    }

}
