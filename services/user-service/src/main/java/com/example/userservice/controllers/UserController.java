package com.example.userservice.controllers;


import com.example.userservice.dtos.*;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController("/users")
@Data
public class UserController {
    final private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegister userRegister){
            var registerResponse= userService.addUser(userRegister);
            return  ResponseEntity.status(201).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLogin userLogin) {
        var loginResponse = userService.login(userLogin);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable UUID userId) {
        var profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }




}
