package com.ejadainternship.vbank.bff_service.dtos;

import java.math.BigDecimal;
import java.util.List;

public record AccountDetailsDTO(
        String accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String status,
        List<AccountTransactionsDTO> transactions
) {
}
