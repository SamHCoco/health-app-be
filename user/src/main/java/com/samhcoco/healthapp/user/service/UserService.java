package com.samhcoco.healthapp.user.service;

import com.samhcoco.healthapp.core.model.Action;
import com.samhcoco.healthapp.core.model.User;

public interface UserService {

    /**
     * Returns the {@link User} specified by the given ID.
     * @param patientId The ID of the {@link User}.
     * @return The specified {@link User}.
     */
    User getUserById(Long patientId);

    /**
     * Registers a new {@link User}.
     * @param user The {@link User} to register.
     * @return The newly registered {@link User}.
     */
    User register(User user);

    /**
     * Verifies an email confirmation code for the given {@link User} ID and enables the associated {@link User}
     * if they are valid.
     * @param code An email verification code sent to a {@link User} following email registration.
     * @param userId A {@link User} ID.
     * @return {@link Action} describing whether the operation succeeded or failed.
     */
    Action verify(String code, Long userId);

}
