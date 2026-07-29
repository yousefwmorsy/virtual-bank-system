    package com.ejadainternship.vbank.bff_service.services;

    import com.ejadainternship.vbank.bff_service.client.AccountsServiceClient;
    import com.ejadainternship.vbank.bff_service.client.TransactionsServiceClient;
    import com.ejadainternship.vbank.bff_service.client.UserServiceClient;
    import com.ejadainternship.vbank.bff_service.dtos.AccountDetailsDTO;
    import com.ejadainternship.vbank.bff_service.dtos.DashboardDTO;
    import com.ejadainternship.vbank.bff_service.dtos.LogItemDTO;
    import com.ejadainternship.vbank.bff_service.dtos.UserDetailsDTO;
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.kafka.core.KafkaTemplate;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.reactive.function.client.WebClientResponseException;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class BFFDashboardService {
        private final AccountsServiceClient accountServiceClient;
        private final UserServiceClient userServiceClient;
        private final TransactionsServiceClient transactionsServiceClient;
        private final KafkaTemplate<String, LogItemDTO> kafkaTemplate;
        private final ObjectMapper objectMapper;

        public Mono<ResponseEntity<DashboardDTO>> getDashboard(@PathVariable String userId) {
            publish("", "Request");
            Mono<UserDetailsDTO> userMono = userServiceClient.getProfileByUser(userId);

            Mono<List<AccountDetailsDTO>> dashboardAccountsMono =
                    accountServiceClient.getAccountsByUser(userId)
                            .flatMap(accounts ->
                                    Flux.fromIterable(accounts)
                                            .flatMap(account ->
                                                    transactionsServiceClient
                                                            .getTransactionsHistoryByUser(account.accountId())
                                                            .onErrorResume(
                                                                    WebClientResponseException.NotFound.class,
                                                                    ex -> Mono.just(List.of())
                                                            )
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
                    }).doOnSuccess(response -> {
                        try {
                            String json = objectMapper.writeValueAsString(response.getBody());
                            publish(json, "Response");
                        } catch (JsonProcessingException e) {
                            // fallback
                            publish(response.getBody().toString(), "Response");
                        }
                    });
        }

        private void publish(String body, String messageType) {
            LogItemDTO logItem = new LogItemDTO(body, messageType, LocalDateTime.now().toString());
            kafkaTemplate.send("microservices-logs", logItem);
        }
    }