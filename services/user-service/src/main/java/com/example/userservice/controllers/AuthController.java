package com.example.userservice.controllers;

import com.example.userservice.dtos.LoginRequest;
import com.example.userservice.dtos.LoginResponse;
import com.example.userservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(
                request.username(), request.password()
        );
        return ResponseEntity.ok(response);
    }
}
