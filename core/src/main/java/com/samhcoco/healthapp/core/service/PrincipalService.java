package com.samhcoco.healthapp.core.service;

import java.security.Principal;
import com.samhcoco.healthapp.core.model.User;

public interface PrincipalService {

    /**
     * Verifies that the {@link Principal} by ensuring its details matches those persisted in the system.
     * @return <code>true</code> if the {@link Principal} has details consistent with those persisted in the system,
     * <code>false otherwise</code>.
     */
    boolean isAuthorized();

    /**
     * Extracts {@link User} ID from the {@link Principal}.
     * @return {@link User} ID or <code>null</code> the operation failed.
     */
    Long getUserId();
}
