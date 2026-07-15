package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.dtos.AccountDetailsDTO;
import com.ejadainternship.vbank.account_service.dtos.MessageDTO;
import com.ejadainternship.vbank.account_service.dtos.TransferRequestDTO;
import com.ejadainternship.vbank.account_service.exceptions.AccountDoesNotExistException;
import com.ejadainternship.vbank.account_service.exceptions.InsufficientBalanceException;
import com.ejadainternship.vbank.account_service.mapper.AccountMapper;
import com.ejadainternship.vbank.account_service.models.Account;
import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public MessageDTO transferAmount(TransferRequestDTO transferRequestDTO) {
        Account fromAccount = accountRepository.findById(transferRequestDTO.fromAccountId()).orElseThrow(AccountDoesNotExistException::new);
        Account toAccount = accountRepository.findById(transferRequestDTO.toAccountId()).orElseThrow(AccountDoesNotExistException::new);
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


}
