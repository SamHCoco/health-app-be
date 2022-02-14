package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> realmRoles;
    private Map<String, List<String>> clientRoles;
    private Boolean enabled;
    private Boolean emailVerified;
    private Integer createdTimestamp;
    private Map<String, String> attributes;
    private List<Credential> credentials;
}
