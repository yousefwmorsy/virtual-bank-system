package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.feign.UserInterface;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserInterface userInterface;

    public UserService(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public boolean verifyUser(String userId){
        var userResponse= userInterface.getProfile(UUID.fromString(userId));
        return userResponse.getStatusCode() == HttpStatus.OK;
    }
}
