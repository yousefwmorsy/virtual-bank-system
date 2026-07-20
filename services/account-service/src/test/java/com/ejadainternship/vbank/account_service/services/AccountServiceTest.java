package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.dtos.*;
import com.ejadainternship.vbank.account_service.exceptions.AccountDoesNotExistException;
import com.ejadainternship.vbank.account_service.exceptions.InsufficientBalanceException;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.models.AccountStatus;
import com.ejadainternship.vbank.account_service.models.AccountType;
import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerationService accountNumberGenerationService;

    @InjectMocks
    private AccountService accountService;

    private Account createActiveAccount(String accountId, String userId, BigDecimal balance) {
        return Account.builder()
                .id(accountId)
                .userId(userId)
                .accountNumber("12345")
                .balance(balance)
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .lastTransactionDate(LocalDateTime.now())
                .build();
    }


    @Test
    @DisplayName("Should return account when ID exists")
    void getAccountById_Exists() {
        String accountId = "Hello";
        String userId = "user-1";
        BigDecimal balance = BigDecimal.valueOf(100);

        Account expectedAccount = createActiveAccount(accountId, userId, balance);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        Account result = accountService.getAccountById(accountId);
        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(balance, result.getBalance());
        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("Should return error message when account ID does not exist")
    void getAccountById_DoesNotExist() {
        String accountId = "DoesNotExist";

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountDoesNotExistException exception = assertThrows(
                AccountDoesNotExistException.class,
                () -> accountService.getAccountById(accountId)
        );
        assertTrue(exception.getMessage().contains(accountId));
        verify(accountRepository).findById(accountId);
    }


}
