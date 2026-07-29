package com.example.userservice.dtos;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}