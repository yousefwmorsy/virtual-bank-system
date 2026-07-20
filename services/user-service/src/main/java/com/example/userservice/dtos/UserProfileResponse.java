package com.example.userservice.dtos;

import lombok.Data;

import java.util.UUID;
@Data
public class UserProfileResponse {
    private UUID userId;
    private String username;
    private String email;
}
