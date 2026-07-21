package com.ejadainternship.vbank.account_service.dtos;

import java.util.UUID;

public record UserProfileDTO(
        UUID userId,
        String username,
        String email
) {
}
