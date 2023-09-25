package com.samhcoco.healthapp.core.service.impl;

import com.google.gson.Gson;
import com.samhcoco.healthapp.core.model.*;
import com.samhcoco.healthapp.core.service.KeycloakService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
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
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientName;

    @Value("${app.keycloak.username:admin}")
    private String username;

    @Value("${app.keycloak.password:admin}")
    private String password;

    @Value("${app.keycloak.grant-type:password}")
    private String grantType;

    @Value("${app.keycloak.secret}")
    private String secret;

    @Value("${keycloak.auth-server-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final Gson gson;

    @PostConstruct
    public void setup () {
        try {
            initialize();
        } catch (HttpClientErrorException e) {
            log.debug(format("Attempted to create Keycloak client '%s' - this client may already exist.", clientName));
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
        try {
            val response = restTemplate.exchange(url, POST, request, KeycloakToken.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to get Keycloak Admin Access Token: {}", e.getMessage());
        }
        return null;
    }

    @Override
    @Deprecated
    public ResponseEntity<KeycloakToken> getAccessToken(@NonNull String username,@NonNull String password) {
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
        body.add("client_secret", secret);
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grantType);

        val request = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(url, POST, request, KeycloakToken.class);
        } catch (Exception e) {
            log.error("Failed to get access token for user {}: {}", username, e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<KeycloakTokenInfo> getTokenInformation(@NonNull String accessToken) {
        val url = format("%srealms/%s/protocol/openid-connect/token/introspect", baseUrl, realm);

        val headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        val body = new LinkedMultiValueMap<String, String>();
        body.add("token", accessToken);
        body.add("client_id", clientName);
        body.add("client_secret", secret);

        val request = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(url, POST, request, KeycloakTokenInfo.class);
        } catch (Exception e) {
            val tokenInfo = new KeycloakTokenInfo();
            tokenInfo.setError(e.getMessage());
            return new ResponseEntity<>(tokenInfo, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public KeycloakUser create(@NonNull KeycloakUser user) {
        val url = format("%sadmin/realms/%s/users", baseUrl, realm);

        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(user, headers);
        try {
            val response = restTemplate.exchange(url, POST, request, KeycloakUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                val location = response.getHeaders().getLocation();
                if (nonNull(location)) {
                    val uri = location.toString().split("/");
                    user.setId(uri[uri.length - 1]);
                }
                log.debug("Successfully created '{}'", user);
                return user;
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to create {}: {}", user, e.getMessage());
        }
        return null;
    }

    @Override
    public ResponseEntity<String> assignRoles(@NonNull String userId, @NonNull Set<String> roles) {
        val url = format("%sadmin/realms/%s/users/%s/role-mappings/realm", baseUrl, realm, userId);

        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val targetRoles = listAvailableRoles(userId).stream()
                                                        .filter(role -> roles.contains(role.getName()))
                                                        .collect(toList());

        val request = new HttpEntity<>(targetRoles, headers);
        try {
            val response = restTemplate.exchange(url, POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Keycloak user with ID '{}' successfully assigned roles: {}", userId, roles);
                return response;
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to assign Keycloak user with ID '{}' roles {}: {}", userId, roles, e.getMessage());
        }
        return null;
    }

    @Override
    public List<KeycloakRole> listAvailableRoles(@NonNull String userId) {
        val url = format("%sadmin/realms/%s/users/%s/role-mappings/realm/available", baseUrl, realm, userId);

        val headers = new HttpHeaders();
        val token = getAdminAccessToken();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(null, headers);
        try {
            val response = restTemplate.exchange(url, GET, request, KeycloakRole[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                val roles = response.getBody();
                if (nonNull(roles)) {
                    return Arrays.stream(roles).collect(toList());
                }
            }
        } catch (RestClientResponseException e) {
            val error = e.getMessage();
            log.error("Failed to list available roles for user with ID '{}' : '{}'", userId, error);
        }
        return emptyList();
    }

    @Override
    public ResponseEntity<String> delete(@NonNull String userId) {
        val url = format("%sadmin/auth/admin/realms/projects/users/%s", baseUrl, userId);

        val headers = new HttpHeaders();
        val token = getAdminAccessToken();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(null, headers);
        try {
            val response = restTemplate.exchange(url, DELETE, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully deleted Keycloak user with ID '{}'", userId);
                return response;
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to delete Keycloak user with ID '{}': {}", userId, e.getMessage());
        }
        return null;
    }

    @Override
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

        try {
            var response = restTemplate.exchange(url, POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully initialized Keycloak client '{}'", clientName);
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to initialize Keycloak client '{}' : '{}'", clientName, e.getMessage());
            return null;
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

        url = format("%sadmin/realms/master/roles", baseUrl);

        request = new HttpEntity(userRole, headers);

        try {
            val response = restTemplate.exchange(url, POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully initialized Keycloak role '{}' for realm '{}'", USER.name().toLowerCase(), realm);
            }
            return response;
        } catch (RestClientResponseException e) {
            log.error("Failed to initialize Keycloak role '{}' for realm '{}'", USER.name().toLowerCase(), realm);
            return null;
        }
    }

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
        try {
            val response = restTemplate.exchange(url, POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully created User Attribute '{} - {}' for Keycloak client '{}'", claimName, type, clientId);
            }
            return response;
        } catch (RestClientResponseException e) {
            log.error("Failed to create User Attribute {} for Keycloak client {}: {}", claimName, clientId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<KeycloakClient> listClients() {
        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val url = format("%sadmin/realms/%s/clients", baseUrl, realm);

        val request = new HttpEntity<>(null, headers);
        try {
            val response = restTemplate.exchange(url, GET, request, KeycloakClient[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                val body = response.getBody();
                if (nonNull(body)) {
                    return Arrays.stream(body).collect(toList());
                }
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to get clients for realm '{}': {}", realm, e.getMessage());
        }
        return emptyList();
    }

    @Override
    public KeycloakUser getUser(@NonNull String id) {
        val url = format("%sadmin/realms/%s/users/%s", baseUrl, realm, id);
        val token = getAdminAccessToken();

        val headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(headers);
        try {
            val response = restTemplate.exchange(url, GET, request, KeycloakUser.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get Keycloak user with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public KeycloakUser updateUser(@NonNull KeycloakUser user) {
        val url = format("%sadmin/realms/%s/users/%s", baseUrl, realm, user.getId());

        val token = getAdminAccessToken();
        val headers = new HttpHeaders();

        headers.setBearerAuth(token.getAccessToken());

        val request = new HttpEntity<>(user, headers);
        try {
            val response = restTemplate.exchange(url, PUT, request, KeycloakUser.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return user;
            }
        } catch (RestClientResponseException e) {
            log.error("Failed to updated Keycloak user with ID {}: {}", user.getId(), e.getMessage());
            return null;
        }
        return null;
    }
}
