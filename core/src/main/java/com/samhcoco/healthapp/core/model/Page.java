package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;

    public PageRequest buildRequest() {
        if (isNull(page)) {
            page = 0;
        } else {
            page--;
        }
        if (isNull(size) || size <= 0) {
            size = 50;
        }

        if (!hasText(sortBy)) {
            sortBy = "id";
        }

        val validDirections = Set.of("ASC", "DESC");
        if (!hasText(direction) || !validDirections.contains(direction)) {
            direction = "DESC";
        }
        return PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortBy);
    }

}
