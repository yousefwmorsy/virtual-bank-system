package com.ejadainternship.vbank.account_service.feign;

import com.ejadainternship.vbank.account_service.dtos.UserProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("USER-SERVICE")
public interface UserInterface {
    @GetMapping("users/{userId}/profile")
    ResponseEntity<UserProfileDTO> getProfile(@PathVariable UUID userId);
}