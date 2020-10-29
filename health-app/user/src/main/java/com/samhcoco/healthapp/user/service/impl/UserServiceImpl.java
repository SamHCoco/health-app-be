package com.samhcoco.healthapp.user.service.impl;

import com.samhcoco.healthapp.core.enums.KeycloakRoles;
import com.samhcoco.healthapp.core.model.KeycloakUser;
import com.samhcoco.healthapp.core.model.User;
import com.samhcoco.healthapp.core.repository.UserRepository;
import com.samhcoco.healthapp.core.service.KeycloakService;
import com.samhcoco.healthapp.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    // todo - add health service

    public User getUserById(@NonNull Long patientId) {
        return userRepository.findById((long) patientId);
    }

    public User register(@NonNull User user) {
        user.setEnabled(true);

        val createdUser = userRepository.save(user);
        val credentials = KeycloakUser.Credential.builder()
                .temporary(false)
                .value(user.getPassword())
                .build();

        var keycloakUser = KeycloakUser.builder()
                .email(user.getEmail())
                .username(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerified(true) // todo - set to false
                .enabled(true)
                .attributes(Map.of("userId", String.valueOf(user.getId())))
                .credentials(List.of(credentials))
                .build();

        keycloakUser = keycloakService.create(keycloakUser);

        if (isNull(keycloakUser)) {
            log.error("Failed to create user " + user + ": could not register the user to Keycloak.");
            userRepository.deleteById(createdUser.getId());
            return null;
        }

        createdUser.setKeycloakId(keycloakUser.getId());

        val roles = keycloakService.assignRoles(keycloakUser.getId(), Set.of(KeycloakRoles.USER.name().toLowerCase()));

        if (!roles.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to assign user " + createdUser  + " Keycloak role: 'user'");
            userRepository.deleteById(createdUser.getId());
            keycloakService.delete(keycloakUser.getId());
            return null;
        }

        val persistedUser = userRepository.save(createdUser);
        return persistedUser;
    }

}
