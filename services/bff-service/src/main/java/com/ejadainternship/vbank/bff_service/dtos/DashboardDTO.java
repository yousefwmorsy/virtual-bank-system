package com.ejadainternship.vbank.bff_service.dtos;

import java.util.List;

public record DashboardDTO(
    String userId,
    String username,
    String email,
    String firstName,
    String lastName,
    List<AccountDetailsDTO> accounts
) {
}
