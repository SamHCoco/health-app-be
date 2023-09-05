package com.samhcoco.healthapp.core.service;

import com.samhcoco.healthapp.core.model.Action;
import com.samhcoco.healthapp.core.model.Message;

public interface MessageService {

    /**
     * Sends the given {@link Message}.
     * @param message {@link Message}.
     * @return An {@link Action} which describes whether the operation was successful or failed.
     */
    Action send(Message message);

}
