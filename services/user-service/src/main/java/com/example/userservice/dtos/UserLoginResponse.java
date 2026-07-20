package com.example.userservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLoginResponse {
    private UUID userId;
    private String username;
}
