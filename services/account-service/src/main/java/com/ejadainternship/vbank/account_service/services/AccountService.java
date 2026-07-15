package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.dtos.MessageDTO;
import com.ejadainternship.vbank.account_service.dtos.TransferRequestDTO;
import com.ejadainternship.vbank.account_service.exceptions.InsufficientBalanceException;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.security.auth.login.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

}
