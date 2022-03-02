package com.samhcoco.healthapp.user.controller;

import com.google.gson.Gson;
import com.samhcoco.healthapp.core.model.Error;
import com.samhcoco.healthapp.core.model.Message;
import com.samhcoco.healthapp.core.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private static final String V1 = "api/v1/message";

    private final EmailServiceImpl emailService;
    private final Gson gson;

    @PostMapping(V1 + "/send")
    public ResponseEntity<String> send(@RequestBody Message message) {
        val action = emailService.send(message);
        if (action.isSuccess()) {
            return new ResponseEntity<>(gson.toJson(message), OK);
        }
        return new ResponseEntity<>(gson.toJson(new Error(action.getMessage())), INTERNAL_SERVER_ERROR);
    }

}
