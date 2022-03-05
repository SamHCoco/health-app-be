package com.samhcoco.healthapp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakUser implements Serializable {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> realmRoles;
    private Map<String, List<String>> clientRoles;
    private Boolean enabled;
    private Boolean emailVerified;
    private Long createdTimestamp;
    private Map<String, Object> attributes;
    private List<Credential> credentials;
}
