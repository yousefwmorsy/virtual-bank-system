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

    @Test
    @DisplayName("Should transfer amount successfully between two accounts")
    void transferAmount_Successfully() {
        Account fromAccount = createActiveAccount("from-1", "user-1", BigDecimal.valueOf(500));
        Account toAccount = createActiveAccount("to-1", "user-2", BigDecimal.valueOf(200));
        TransferRequestDTO request = new TransferRequestDTO("from-1", "to-1", BigDecimal.valueOf(100));

        when(accountRepository.findById("from-1")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById("to-1")).thenReturn(Optional.of(toAccount));

        MessageDTO result = accountService.transferAmount(request);

        assertEquals("Account updated successfully", result.message());
        assertEquals(BigDecimal.valueOf(400), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(300), toAccount.getBalance());
        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
    }

    @Test
    @DisplayName("Should throw exception when from-account has insufficient balance")
    void transferAmount_InsufficientBalance() {
        Account fromAccount = createActiveAccount("from-1", "user-1", BigDecimal.valueOf(50));
        Account toAccount = createActiveAccount("to-1", "user-2", BigDecimal.valueOf(200));
        TransferRequestDTO request = new TransferRequestDTO("from-1", "to-1", BigDecimal.valueOf(100));

        when(accountRepository.findById("from-1")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById("to-1")).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientBalanceException.class, () -> accountService.transferAmount(request));

        assertEquals(BigDecimal.valueOf(50), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(200), toAccount.getBalance());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when from-account does not exist")
    void transferAmount_FromAccountNotFound() {
        TransferRequestDTO request = new TransferRequestDTO("non-existent", "to-1", BigDecimal.valueOf(100));
        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(AccountDoesNotExistException.class, () -> accountService.transferAmount(request));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when to-account does not exist")
    void transferAmount_ToAccountNotFound() {
        Account fromAccount = createActiveAccount("from-1", "user-1", BigDecimal.valueOf(500));
        TransferRequestDTO request = new TransferRequestDTO("from-1", "non-existent", BigDecimal.valueOf(100));

        when(accountRepository.findById("from-1")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(AccountDoesNotExistException.class, () -> accountService.transferAmount(request));
        verify(accountRepository, never()).save(any());
    }
}
