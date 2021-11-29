package com.samhcoco.healthapp.user.controller;

import com.samhcoco.healthapp.core.model.User;
import com.samhcoco.healthapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public User getUserId(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody User user) {
        val registered = userService.register(user);
        return new ResponseEntity<>(registered, HttpStatus.OK);
    }


}
