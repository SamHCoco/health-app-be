package com.samhcoco.healthapp.core.service;

import com.samhcoco.healthapp.core.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface KeycloakService {

    /**
     * Returns an {@link KeycloakToken}.
     * @return A {@link KeycloakToken}.
     */
    KeycloakToken getAdminAccessToken();

    /**
     * Returns a user {@link KeycloakToken}.
     * @param username Username.
     * @param password Password.
     * @return {@link KeycloakToken}.
     */
    ResponseEntity<KeycloakToken> getAccessToken(String username, String password);

    /**
     * Creates a {@link KeycloakUser}.
     * @param user The {@link KeycloakUser}.
     * @return The created {@link KeycloakUser}.
     */
    KeycloakUser create(KeycloakUser user);

    /**
     * Assigns the specified roles to the given {@link KeycloakUser}.
     * @param userId {@link KeycloakUser} Id.
     * @param roles  Roles to be assigned to the user.
     * @return {@link ResponseEntity}.
     */
    ResponseEntity<String> assignRoles(String userId, Set<String> roles);

    /**
     * Lists the available {@link KeycloakRole} for the given {@link KeycloakUser}.
     * @param userId The {@link KeycloakUser} ID.
     * @return Returns collection of available {@link KeycloakRole}.
     */
    List<KeycloakRole> listAvailableRoles(String userId);

    /**
     * Deletes a {@link KeycloakUser} from keycloak.
     * @param userId The {@link KeycloakUser} ID.
     * @return A {@link ResponseEntity}.
     */
    ResponseEntity<String> delete(String userId);

    /**
     * Initialises the keycloak client for the application if does not exist.
     * @return A {@link ResponseEntity}
     */
    ResponseEntity<String> initialize();

    /**
     * Creates an token attribute that may be mapped to a user.
     * @param claimName The attribute name.
     * @param type The attribute type.
     * @param clientId The Keycloak client ID.
     * @return A {@link ResponseEntity}.
     */
    ResponseEntity<String> createUserAttribute(String claimName, String type, String clientId);


    List<KeycloakClient> listClients();

    ResponseEntity<KeycloakTokenInfo> getTokenInformation(String accessToken);

    KeycloakUser getUser(String id);

    KeycloakUser updateUser(KeycloakUser user);



}