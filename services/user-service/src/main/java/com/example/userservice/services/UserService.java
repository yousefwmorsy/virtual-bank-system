package com.example.userservice.services;

import com.example.userservice.dtos.*;
import com.example.userservice.exceptions.InvalidCredentialsException;
import com.example.userservice.exceptions.UserAlreadyExistsException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.models.User;
import com.example.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterResponse addUser(UserRegister userRegister) {
        if (userRepository.existsByUsernameOrEmail(userRegister.getUsername(), userRegister.getEmail())) {
            throw new UserAlreadyExistsException("Username or email already exists.");
        }
        User user = userMapper.toEntity(userRegister);
        user.setPasswordHash(passwordEncoder.encode(userRegister.getPassword()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserLoginResponse login(UserLogin userLogin) {
        User user = userRepository.findByUsername(userLogin.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));
        if (!passwordEncoder.matches(userLogin.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        AuthResponse authResponse = authService.authenticate(userLogin.getUsername(), userLogin.getPassword());
        UserLoginResponse loginResponse = userMapper.toLoginDto(user);
        loginResponse.setAuthResponse(authResponse);
        return loginResponse;
    }

    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        return userMapper.toProfileDto(user);
    }
}