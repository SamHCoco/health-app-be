package com.samhcoco.healthapp.store.model;

import com.samhcoco.healthapp.core.model.Page;
import com.samhcoco.healthapp.core.model.SearchCriteria;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductPage extends Page {

    private Float price;
    private String priceOperation;
    private String manufacturer;

    public List<SearchCriteria> buildSearchCriteria() {
        val searchCriteria = new ArrayList<SearchCriteria>();

        if (nonNull(price) && hasText(priceOperation)) {
            searchCriteria.add(new SearchCriteria("price", String.valueOf(price), priceOperation));
        }

        if (hasText(manufacturer)) {
            searchCriteria.add(new SearchCriteria("manufacturer", manufacturer, "=="));
        }
        return searchCriteria;
    }

}
