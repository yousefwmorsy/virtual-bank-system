package com.ejadainternship.vbank.bff_service.services;

import com.ejadainternship.vbank.bff_service.client.AccountsServiceClient;
import com.ejadainternship.vbank.bff_service.client.TransactionsServiceClient;
import com.ejadainternship.vbank.bff_service.client.UserServiceClient;
import com.ejadainternship.vbank.bff_service.dtos.AccountDetailsDTO;
import com.ejadainternship.vbank.bff_service.dtos.DashboardDTO;
import com.ejadainternship.vbank.bff_service.dtos.UserDetailsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BFFDashboardService {
    private final AccountsServiceClient accountServiceClient;
    private final UserServiceClient userServiceClient;
    private final TransactionsServiceClient transactionsServiceClient;

    public BFFDashboardService(AccountsServiceClient accountServiceClient, UserServiceClient userServiceClient, TransactionsServiceClient transactionsServiceClient) {
        this.accountServiceClient = accountServiceClient;
        this.userServiceClient = userServiceClient;
        this.transactionsServiceClient = transactionsServiceClient;
    }

    public Mono<ResponseEntity<DashboardDTO>> getDashboard(@PathVariable String userId) {
        Mono<UserDetailsDTO> userMono = userServiceClient.getProfileByUser(userId);

        Mono<List<AccountDetailsDTO>> dashboardAccountsMono =
                accountServiceClient.getAccountsByUser(userId)
                        .flatMap(accounts ->
                                Flux.fromIterable(accounts)
                                        .flatMap(account ->
                                                transactionsServiceClient
                                                        .getTransactionsHistoryByUser(account.accountId())
                                                        .map(transactions ->
                                                                new AccountDetailsDTO(
                                                                        account.accountId(),
                                                                        account.accountNumber(),
                                                                        account.accountType(),
                                                                        account.balance(),
                                                                        account.status(),
                                                                        transactions
                                                                )
                                                        )
                                        )
                                        .collectList()
                        );

        return Mono.zip(userMono, dashboardAccountsMono)
                .map(tuple -> {
                    UserDetailsDTO user = tuple.getT1();

                    return ResponseEntity.ok(
                            new DashboardDTO(
                                    user.userId(),
                                    user.username(),
                                    user.email(),
                                    user.firstName(),
                                    user.lastName(),
                                    tuple.getT2()
                            )
                    );
                });
    }
}