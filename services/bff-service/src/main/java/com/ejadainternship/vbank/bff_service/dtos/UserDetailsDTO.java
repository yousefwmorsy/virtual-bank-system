package com.ejadainternship.vbank.bff_service.dtos;

public record UserDetailsDTO(
        String userId,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
