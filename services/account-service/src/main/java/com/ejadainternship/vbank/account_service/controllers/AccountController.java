package com.ejadainternship.vbank.account_service.controllers;

import com.ejadainternship.vbank.account_service.dtos.*;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PutMapping("/transfer")
    public MessageDTO transferAmount(@RequestBody TransferRequestDTO transferRequestDTO) {
        return null;
    }

    @GetMapping("/{account-id}")
    public AccountDetailsDTO getAccount(@PathVariable("account-id") String accountId) {
        return null;
    }

    @PostMapping
    public AccountSummaryDTO createAccount(@RequestBody CreateAccountRequestDTO account) {
        return null;
    }
}
