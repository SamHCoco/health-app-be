package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakRole implements Serializable {
    private String id;
    private String name;
    private boolean composite;
    private boolean clientRole;
    private String containerId;
}
