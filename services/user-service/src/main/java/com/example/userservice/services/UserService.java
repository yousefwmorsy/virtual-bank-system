package com.example.userservice.services;

import com.example.userservice.dtos.UserRegister;
import com.example.userservice.dtos.UserRegisterResponse;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.repositories.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class UserService {
    final private UserRepository userRepository;
    final private UserMapper userMapper;
    public UserRegisterResponse addUser(UserRegister userRegister){
        var user =  userMapper.toEntity(userRegister);
        userRepository.save(user);
        return userMapper.toDto(user);
    }



}
