package com.ejadainternship.vbank.account_service.controllers;

import com.ejadainternship.vbank.account_service.dtos.AccountDetailsDTO;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final AccountService accountService;

    @GetMapping("/{user-id}/accounts")
    public List<AccountDetailsDTO> getAccounts(@PathVariable("user-id") String userId) {
        return accountService.getAccounts(userId);
    }
}
