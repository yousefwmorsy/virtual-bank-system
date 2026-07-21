package com.ejadainternship.vbank.account_service.scheduled;

import com.ejadainternship.vbank.account_service.models.AccountStatus;
import com.ejadainternship.vbank.account_service.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AccountStatusJob {
    private final AccountRepository accountRepository;

    public AccountStatusJob(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Scheduled(fixedRate = 60*60*1_000) // 60 minutes * 60 seconds * 1000 milliseconds = 1 hour
    @Transactional
    public void markInactiveAccounts() {
        log.info("Running AccountStatusJob...");
        LocalDateTime allowedLastActive = LocalDateTime.now().minusDays(1);
        AtomicInteger inactiveAccountsCount = new AtomicInteger();
        accountRepository.findAllByLastTransactionDateBeforeAndStatusEquals(allowedLastActive, AccountStatus.ACTIVE).forEach(
                account -> {
                    account.setStatus(AccountStatus.INACTIVE);
                    inactiveAccountsCount.getAndIncrement();
                    accountRepository.save(account);
                }
        );
        log.info("Inactive accounts marked: {}", inactiveAccountsCount.get());
    }
}
