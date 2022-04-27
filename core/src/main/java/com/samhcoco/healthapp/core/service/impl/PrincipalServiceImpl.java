package com.samhcoco.healthapp.core.service.impl;

import com.samhcoco.healthapp.core.repository.UserRepository;
import com.samhcoco.healthapp.core.service.PrincipalService;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.security.Principal;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService {

    private final UserRepository userRepository;

    @Override
    public boolean isAuthorized() {
        val authentication = SecurityContextHolder.getContext().getAuthentication();
        val principal = (Principal) authentication.getPrincipal();

        if (isKeycloakPrincipal(principal)) {
            val userId = getUserId();
            if (isNull(userId)) {
                return false;
            }

            val user = userRepository.findById((long) userId);
            if (isNull(user)) {
                return false;
            }

            val keycloakId = user.getKeycloakId();
            return keycloakId.equals(principal.getName());
        }
        return false;
    }

    @Override
    public Long getUserId() {
        val authentication = SecurityContextHolder.getContext().getAuthentication();
        val principal = (Principal) authentication.getPrincipal();

        if (isKeycloakPrincipal(principal)) {
            val keycloakPrincipal = (KeycloakPrincipal) principal;
            val idToken = keycloakPrincipal.getKeycloakSecurityContext().getIdToken();

            try {
                val userId = (String) idToken.getOtherClaims().get("userId");
                log.debug("PRINCIPAL {}: TOKEN USER ID = {}", principal, userId);
                return parseLong(userId);
            } catch (NumberFormatException e) {
                log.error("Failed to parse user ID claim from " + principal + ": " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Determines whether the current {@link Principal} is an instance of {@link KeycloakPrincipal}.
     * @param principal {@link Principal}.
     * @return <code>true</code> if
     */
    private boolean isKeycloakPrincipal(Principal principal) {
        return principal instanceof KeycloakPrincipal;
    }
}
