package com.samhcoco.healthapp.user.controller;

import com.samhcoco.healthapp.core.model.User;
import com.samhcoco.healthapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User getUserId(@PathVariable Long id) {
        return userService.getUserById(id);
    }


}

