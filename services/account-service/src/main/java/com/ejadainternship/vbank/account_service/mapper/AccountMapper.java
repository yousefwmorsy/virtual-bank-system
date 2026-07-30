package com.ejadainternship.vbank.account_service.mapper;

import com.ejadainternship.vbank.account_service.dtos.AccountDetailsDTO;
import com.ejadainternship.vbank.account_service.dtos.AccountSummaryDTO;
import com.ejadainternship.vbank.account_service.models.Account;

public class AccountMapper {
    public static AccountDetailsDTO toAccountDetailsDTO(Account account){
        return new AccountDetailsDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType().toString(),
                account.getBalance(),
                account.getStatus().toString()
        );
    }

    public static AccountSummaryDTO toAccountSummaryDTO(Account account){
        return new AccountSummaryDTO(
                account.getId(),
                account.getAccountNumber(),
                "Account created successfully"
        );
    }
}
