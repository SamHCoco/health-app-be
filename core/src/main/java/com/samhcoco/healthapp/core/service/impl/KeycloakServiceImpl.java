package com.samhcoco.healthapp.core.service.impl;

import com.samhcoco.healthapp.core.model.KeycloakClient;
import com.samhcoco.healthapp.core.model.KeycloakRole;
import com.samhcoco.healthapp.core.model.KeycloakToken;
import com.samhcoco.healthapp.core.model.KeycloakUser;
import com.samhcoco.healthapp.core.service.KeycloakService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.samhcoco.healthapp.core.enums.KeycloakRoles.USER;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientName;

    @Value("${app.keycloak.username}")
    private String username;

    @Value("${app.keycloak.password}")
    private String password;

    @Value("${app.keycloak.grant-type}")
    private String grantType;

    @Value("${keycloak.auth-server-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @PostConstruct
    public void setup () {
        try {
            initialize();
        } catch (HttpClientErrorException e) {
            log.debug(format("Attempted to create Keycloak client '%s' - no action required - This client already exists.", clientName));
        }
    }

    @Override
    public KeycloakToken getAdminAccessToken() {
        val url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("realms/")
                .path(realm)
                .path("/protocol")
                .path("/openid-connect")
                .path("/token")
                .toUriString();

        val headers = new HttpHeaders();

        headers.setContentType(APPLICATION_FORM_URLENCODED);

        val body = new LinkedMultiValueMap<String, String>();

        body.add("client_id", "admin-cli");
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grantType);


        val request = new HttpEntity<LinkedMultiValueMap<String, String>>(body, headers);
        val response = restTemplate.exchange(url, POST, request, KeycloakToken.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            val error = response.getStatusCode().getReasonPhrase();
            log.error("Failed to get Keycloak Admin Access Token: " + error);
        }
        return null;
    }

    @Override
    @Deprecated
    public KeycloakToken getAccessToken(@NonNull String username,@NonNull String password) {
        // todo - remove
        val url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("realms/")
                .path(realm)
                .path("/protocol")
                .path("/openid-connect")
                .path("/token")
                .toUriString();

        val headers = new HttpHeaders();

        headers.setContentType(APPLICATION_FORM_URLENCODED);

        val body = new LinkedMultiValueMap<String, String>();

        body.add("client_id", clientName);
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grantType);


        val request = new HttpEntity<LinkedMultiValueMap<String, String>>(body, headers);
        val response = restTemplate.exchange(url, POST, request, KeycloakToken.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            val error = response.getStatusCode().getReasonPhrase();
            log.error("Failed to get Keycloak user Access Token: " + error);
        }
        return null;
    }

    public KeycloakUser create(@NonNull KeycloakUser user) {
        val url = format("%sadmin/realms/%s/users", baseUrl, realm);

        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(user, headers);
        val response = restTemplate.exchange(url, POST, request, KeycloakUser.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            val location = response.getHeaders().getLocation();

            // get created keycloak user id
            if (!isNull(location)) {
                val uri = location.toString().split("/");
                user.setId(uri[uri.length - 1]);
            }

            log.debug("Successfully created " + user + ".");
            return user;
        }
        log.error("Failed to create " + user);
        return null;
    }

    // POST http://localhost:8180/auth/admin/realms/{realm}/users/{userId}/role-mappings/realm
    public ResponseEntity<String> assignRoles(@NonNull String userId, @NonNull Set<String> roles) {
        val url = format("%sadmin/realms/%s/users/%s/role-mappings/realm", baseUrl, realm, userId);

        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val targetRoles = listAvailableRoles(userId).stream()
                .filter(role -> roles.contains(role.getName()))
                .collect(toList());

        val request = new HttpEntity<>(targetRoles, headers);
        val response = restTemplate.exchange(url, POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.debug("Keycloak user with ID '%s' successfully assigned roles " + roles);
            return response;
        }
        log.error("Failed to assigned Keycloak user with ID '%s' roles " + roles);
        return response;
    }


    // GET http://localhost:8280/auth/admin/realms/{realm}/users/{userId}/role-mappings/realm/available
    public List<KeycloakRole> listAvailableRoles(@NonNull String userId) {
        val url = format("%sadmin/realms/%s/users/%s/role-mappings/realm/available", baseUrl, realm, userId);

        val headers = new HttpHeaders();
        val token = getAdminAccessToken();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(null, headers);
        val response = restTemplate.exchange(url, GET, request, KeycloakRole[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            val roles = response.getBody();
            if (nonNull(roles)) {
                return Arrays.stream(roles).collect(toList());
            }
        }
        val error = response.getStatusCode().getReasonPhrase();
        log.error("Failed to list available roles for user with ID " + userId + ": " + error);
        return emptyList();
    }

    // DELETE http://localhost:8180/auth/admin/realms/heroes/users/83c72e88-7ac9-4fc7-a7fb-97736d67d261
    public ResponseEntity<String> delete(@NonNull String userId) {
        val url = format("%sadmin/auth/admin/realms/projects/users/%s", baseUrl, userId);

        val headers = new HttpHeaders();
        val token = getAdminAccessToken();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(null, headers);
        val response = restTemplate.exchange(url, DELETE, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.debug("Successfully deleted Keycloak user with ID " + userId);
            return response;
        }
        log.error("Failed to delete Keycloak user with ID " + userId);
        return response;
    }

    public ResponseEntity<String> initialize() throws HttpClientErrorException {
        val headers = new HttpHeaders();
        val token = getAdminAccessToken();

        headers.setBearerAuth(token.getAccessToken());

        var client = KeycloakClient.builder()
                .clientId(clientName)
                .name(clientName)
                .implicitFlowEnabled(true)
                .directAccessGrantsEnabled(true)
                .publicClient(true)
                .redirectUris(List.of("http://localhost:8899/*"))
                .build();

        var url = format("%sadmin/realms/%s/clients", baseUrl, realm);

        var request = new HttpEntity<>(client, headers);
        var response = restTemplate.exchange(url, POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.debug("Successfully initialized Keycloak client '" + clientName + "'");
        } else {
            log.error("Failed to initialize Keycloak client '" + clientName + "'");
            return response;
        }

        client = listClients().stream()
                .filter(keycloakClient -> keycloakClient.getClientId().equals(clientName))
                .findFirst()
                .orElse(null);

        if (nonNull(client)) {
            createUserAttribute("userId", "long", client.getId());
        }

        val userRole = KeycloakRole.builder()
                .name(USER.name().toLowerCase())
                .clientRole(true)
                .composite(false)
                .containerId(realm)
                .build();

        // POST http://localhost:8280/auth/admin/realms/master/roles
        url = format("%sadmin/realms/master/roles", baseUrl);

        request = new HttpEntity(userRole, headers);
        response = restTemplate.exchange(url, POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.debug(format("Successfully initialized Keycloak role '%s' for realm '%s'", USER.name().toLowerCase(), realm));
        } else {
            log.error(format("Failed to initialize Keycloak role '%s' for realm '%s'", USER.name().toLowerCase(), realm));
        }
        return response;
    }

    // http://localhost:8280/auth/admin/realms/projects/clients/18aaf131-7bd4-4bde-99e1-5b9d91bc9de3/protocol-mappers/models
    @Override
    public ResponseEntity<String> createUserAttribute(@NonNull String claimName,
                                                      @NonNull String type,
                                                      @NonNull String clientId) {
        val mapper = new HashMap<String, Object>();

        mapper.put("name", claimName);
        mapper.put("protocol", "openid-connect");
        mapper.put("protocolMapper", "oidc-usermodel-attribute-mapper");
        mapper.put("config", Map.of(
                "id.token.claim", true,
                "claim.name", claimName,
                "user.attribute", claimName,
                "jsonType.label", type,
                "access.token.claim", true,
                "userinfo.token.claim", true
        ));

        val token = getAdminAccessToken();
        val header = new HttpHeaders();

        header.setBearerAuth(token.getAccessToken());

        val url = format("%sadmin/realms/%s/clients/%s/protocol-mappers/models", baseUrl, realm, clientId);

        val request = new HttpEntity(mapper, header);
        val response = restTemplate.exchange(url, POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.debug("Failed created Protocol Mapping for token claim: " + claimName);
        }
        return response;
    }

    // http://localhost:8080/auth/admin/realms/master/clients
    @Override
    public List<KeycloakClient> listClients() {
        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val url = format("%sadmin/realms/%s/clients", baseUrl, realm);

        val request = new HttpEntity<>(null, headers);
        val response = restTemplate.exchange(url, GET, request, KeycloakClient[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            val body = response.getBody();
            if (nonNull(body)) {
                return Arrays.stream(body).collect(toList());
            }
        }
        log.error(format("Failed to get clients for realm '%s': %s", realm, response.getStatusCode().getReasonPhrase()));
        return emptyList();
    }
}
