package com.example.userservice.controllers;


import com.example.userservice.dtos.UserRegister;
import com.example.userservice.dtos.UserRegisterResponse;
import com.example.userservice.services.UserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/users")
@Data
public class UserController {
    final private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(UserRegister userRegister){
            var registerResponse= userService.addUser(userRegister);
            return  ResponseEntity.status(201).body(registerResponse);
    }



}
