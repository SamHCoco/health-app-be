package com.samhcoco.healthapp.user.service.impl;

import com.samhcoco.healthapp.core.enums.KeycloakRoles;
import com.samhcoco.healthapp.core.model.*;
import com.samhcoco.healthapp.core.repository.MessageRepository;
import com.samhcoco.healthapp.core.repository.UserRepository;
import com.samhcoco.healthapp.core.repository.VerificationCodeRepository;
import com.samhcoco.healthapp.core.service.KeycloakService;
import com.samhcoco.healthapp.core.service.impl.EmailServiceImpl;
import com.samhcoco.healthapp.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.samhcoco.healthapp.core.enums.MessageChannel.EMAIL;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${base.uri}")
    private String baseUri;

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailServiceImpl emailService;
    private final MessageRepository messageRepository;

    public User getUserById(@NonNull Long patientId) {
        return userRepository.findById((long) patientId);
    }

    public User register(@NonNull User user) {
        // todo - remove password persistence
        user.setEnabled(true);

        val createdUser = userRepository.save(user);
        val credentials = Credential.builder()
                                                .temporary(false)
                                                .value(user.getPassword())
                                                .build();

        var keycloakUser = KeycloakUser.builder()
                .email(user.getEmail())
                .username(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerified(true) // todo - set to false
                .enabled(true) // todo - set to false
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
        log.info("User successfully registered: {}", persistedUser);

        sendVerificationEmail(user);

        return persistedUser;
    }

    @Override
    public Action verify(@NonNull String code, @NonNull Long userId) {
        val action = new Action();
        val verification = verificationCodeRepository.findByCodeAndUserIdAndVerifiedFalse(code, userId);
        if (nonNull(verification)) {
            verification.setVerified(true);
            action.setSuccess(true);
            // todo - set user to enabled in keycloak
        }
        return action;
    }

    private Action sendVerificationEmail(@NonNull User user) {
        val code = createVerificationCode(user);
        val url = format("%s/user/%s/verify?code=%s", baseUri, user.getId(), code.getCode());
        val body = format("Verify your email: <a href=\"%s\">%s</a>", url, url);

        val message = messageRepository.save(Message.builder()
                                                        .subject("Health App - Confirm your Email")
                                                        .recipient(user.getEmail())
                                                        .body(body)
                                                        .channel(EMAIL.name())
                                                        .build());
        try {
            return emailService.send(message);
        } catch (Exception e) {
            log.error("Failed to send verification email to '{}': {}", user.getEmail(), e.getMessage());
            return Action.builder()
                         .success(false)
                         .message(e.getMessage())
                         .build();
        }
    }

    private VerificationCode createVerificationCode(@NonNull User user) {
        return verificationCodeRepository.save(VerificationCode.builder()
                                                .code(DigestUtils.md5Hex(user.getKeycloakId()))
                                                .userId(user.getId())
                                                .build());
    }

}
