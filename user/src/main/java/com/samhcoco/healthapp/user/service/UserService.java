package com.samhcoco.healthapp.user.service;

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

}
