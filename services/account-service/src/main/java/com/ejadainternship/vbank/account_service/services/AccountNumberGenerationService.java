package com.ejadainternship.vbank.account_service.services;

import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AccountNumberGenerationService {

    @Value("${account.number-generation-service.max-attempts}")
    private int MAX_ATTEMPTS;
    private final AccountRepository accountRepository;
    private final SecureRandom random;

    public AccountNumberGenerationService(AccountRepository accountRepository, SecureRandom random) {
        this.accountRepository = accountRepository;
        this.random = random;
    }

    public String generate() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String candidate = randomTenDigit();
            if (!accountRepository.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate a unique account number after " + MAX_ATTEMPTS + " attempts. Please try again later.");
    }

    private String randomTenDigit() {
        long smallestTenDigit = 1_000_000_000L;
        long range = 9_000_000_000L;

        long randomNumber = smallestTenDigit + (Math.abs(random.nextLong()) % range);
        return String.valueOf(randomNumber);
    }
}
