package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakClient implements Serializable {
    private String id;
    private String clientId;
    private String name;
    private boolean directAccessGrantsEnabled;
    private boolean implicitFlowEnabled;
    private List<String> redirectUris;
    private boolean publicClient;
}
