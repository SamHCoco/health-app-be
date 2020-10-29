package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address implements Serializable {
    private String line1;
    private String line2;
    private String city;
    private String postcode;
    private String country;
}
