package com.example.userservice.dtos;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}