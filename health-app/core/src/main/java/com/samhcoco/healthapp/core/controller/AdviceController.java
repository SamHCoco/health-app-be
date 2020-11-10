package com.samhcoco.healthapp.core.controller;

import com.samhcoco.healthapp.core.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> NotFoundException(String message) {
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

}
