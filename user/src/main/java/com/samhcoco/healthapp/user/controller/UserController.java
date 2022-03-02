package com.samhcoco.healthapp.user.controller;

import com.samhcoco.healthapp.core.model.KeycloakToken;
import com.samhcoco.healthapp.core.model.KeycloakTokenInfo;
import com.samhcoco.healthapp.core.model.User;
import com.samhcoco.healthapp.core.service.KeycloakService;
import com.samhcoco.healthapp.user.model.LoginCredential;
import com.samhcoco.healthapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private static final String V1 = "api/v1/user";

    private final UserService userService;
    private final KeycloakService keycloakService;

    @GetMapping(V1 + "/{id}")
    public User getUserId(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping(V1 + "/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        val registered = userService.register(user);
        return new ResponseEntity<>(registered, OK);
    }

    @PostMapping(V1 + "/{id}/verify")
    public ResponseEntity<Object> verify(@RequestParam String code,
                                         @PathVariable Long id) {
        val action = userService.verify(code, id);
        if (action.isSuccess()) {
            return new ResponseEntity<>(OK);
        }
        return new ResponseEntity<>(BAD_REQUEST);
    }

    @PostMapping(V1 + "/token-info")
    public ResponseEntity<KeycloakTokenInfo> getTokenInformation(@RequestBody KeycloakToken token) {
        // todo - remove variable
        val isAuthenticated = keycloakService.getTokenInformation(token.getAccessToken());
        log.debug("isAuthenticated: {}", isAuthenticated.getBody().isActive());
        return isAuthenticated;
    }

    @Deprecated
    @PostMapping(V1 + "/token")
    public ResponseEntity<KeycloakToken> getUserAccessToken(@RequestBody LoginCredential credential) {
        // todo - remove
        return keycloakService.getAccessToken(credential.getUsername(), credential.getPassword());
    }


}

