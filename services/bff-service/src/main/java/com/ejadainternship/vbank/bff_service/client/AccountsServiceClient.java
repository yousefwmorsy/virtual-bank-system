package com.ejadainternship.vbank.bff_service.client;

import com.ejadainternship.vbank.bff_service.dtos.AccountDetailsDTO;
import com.ejadainternship.vbank.bff_service.exceptions.DownstreamServiceException;
import com.ejadainternship.vbank.bff_service.utils.ServiceResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class AccountsServiceClient {

    private final WebClient webClient;
    private final ServiceResolver serviceResolver;

    public AccountsServiceClient
            (WebClient webClient, ServiceResolver serviceResolver) {
        this.webClient = webClient;
        this.serviceResolver = serviceResolver;
    }

    public Mono<List<AccountDetailsDTO>> getAccountsByUser(String userId, String auth) {
        String baseUrl = serviceResolver.resolveBaseUrl("account-service");

        return webClient.get()
                .uri(baseUrl + "/users/{userId}/accounts", userId)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(new DownstreamServiceException("Accounts Service"))
                )
                .bodyToFlux(AccountDetailsDTO.class)
                .timeout(Duration.ofSeconds(60))
                .collectList();
    }


}