package com.ejadainternship.vbank.bff_service.client;

import com.ejadainternship.vbank.bff_service.dtos.AccountTransactionsDTO;
import com.ejadainternship.vbank.bff_service.exceptions.DownstreamServiceException;
import com.ejadainternship.vbank.bff_service.utils.ServiceResolver;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class TransactionsServiceClient {
    private final WebClient webClient;
    private final ServiceResolver serviceResolver;

    public TransactionsServiceClient(WebClient webClient, ServiceResolver serviceResolver) {
        this.webClient = webClient;
        this.serviceResolver = serviceResolver;
    }

    public Mono<List<AccountTransactionsDTO>> getTransactionsHistoryByUser(String accountId) {
        String baseUrl = serviceResolver.resolveBaseUrl("transaction-service");
        return webClient.get()
                .uri(baseUrl + "/accounts/{accountId}/transactions", accountId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(new DownstreamServiceException("Transactions Service"))
                )
                .bodyToFlux(AccountTransactionsDTO.class)
                .timeout(Duration.ofSeconds(60))
                .collectList();
    }
}
