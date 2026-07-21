package com.ejadainternship.vbank.account_service.controllers;

import com.ejadainternship.vbank.account_service.dtos.*;
import com.ejadainternship.vbank.account_service.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PutMapping("/transfer")
    public MessageDTO transferAmount(@Valid @RequestBody TransferRequestDTO transferRequestDTO) {
        return accountService.transferAmount(transferRequestDTO);
    }

    @GetMapping("/{account-id}")
    public AccountDetailsDTO getAccount(@PathVariable("account-id") String accountId) {
        return accountService.getAccount(accountId);
    }

    @PostMapping
    public AccountSummaryDTO createAccount(@Valid @RequestBody CreateAccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(accountRequestDTO);
    }
}
