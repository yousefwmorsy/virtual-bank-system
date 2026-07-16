package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.dtos.*;
import com.ejadainternship.vbank.account_service.exceptions.AccountDoesNotExistException;
import com.ejadainternship.vbank.account_service.exceptions.InsufficientBalanceException;
import com.ejadainternship.vbank.account_service.mapper.AccountMapper;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.models.AccountStatus;
import com.ejadainternship.vbank.account_service.models.AccountType;
import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerationService accountNumberGenerationService;

    public Account getAccountById(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(
                () -> new AccountDoesNotExistException(accountId)
        );
    }

    @Transactional
    public MessageDTO transferAmount(TransferRequestDTO transferRequestDTO) {
        Account fromAccount = getAccountById(transferRequestDTO.fromAccountId());
        Account toAccount = getAccountById(transferRequestDTO.toAccountId());
        if (fromAccount.getBalance().compareTo(transferRequestDTO.amount()) < 0) {
            throw new InsufficientBalanceException();
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferRequestDTO.amount()));
        toAccount.setBalance(toAccount.getBalance().add(transferRequestDTO.amount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return new MessageDTO("Account updated successfully");
    }

    public List<AccountDetailsDTO> getAccounts(String userId) {
        return accountRepository.findAllByUserId(userId).stream().map(AccountMapper::toAccountDetailsDTO).toList();
    }

    public AccountDetailsDTO getAccount(String accountId) {
        return AccountMapper.toAccountDetailsDTO(
                getAccountById(accountId)
        );
    }

    public AccountSummaryDTO createAccount(CreateAccountRequestDTO accountRequestDTO) {
        String accountNumber = accountNumberGenerationService.generate();
        Account newAccount = Account.builder()
                    .userId(accountRequestDTO.userId())
                    .accountNumber(accountNumber)
                    .balance(accountRequestDTO.initialBalance())
                    .accountType(accountRequestDTO.accountType())
                    .lastTransactionDate(LocalDateTime.now())
                    .status(AccountStatus.ACTIVE)
                    .build();
        accountRepository.save(newAccount);
        return AccountMapper.toAccountSummaryDTO(newAccount);
    }
}
