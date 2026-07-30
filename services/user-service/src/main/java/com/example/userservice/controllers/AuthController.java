package com.example.userservice.controllers;

import com.example.userservice.dtos.AuthResponse;
import com.example.userservice.dtos.UserLogin;
import com.example.userservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader("X-Client-Type") String clientType, @RequestBody UserLogin request) {
        AuthResponse response = authService.authenticate(
                request.getUsername(), request.getPassword(),  clientType
        );
        return ResponseEntity.ok(response);
    }
}
