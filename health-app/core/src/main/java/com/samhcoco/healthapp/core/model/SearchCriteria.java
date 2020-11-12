package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    private String field;
    private String value;
    private String operation; // >, >=, <, <=, ==,like

}
