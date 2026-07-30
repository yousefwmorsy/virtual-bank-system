package com.example.userservice.dtos;


import lombok.Data;

import java.util.UUID;

@Data
public class UserRegisterResponse {

    private UUID userId;

    private String username;

    private String message= "User registered successfully.";
}
