package com.ejadainternship.vbank.bff_service.client;

import com.ejadainternship.vbank.bff_service.dtos.UserDetailsDTO;
import com.ejadainternship.vbank.bff_service.utils.ServiceResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class UserServiceClient {
    private final WebClient webClient;
    private final ServiceResolver serviceResolver;

    public UserServiceClient(WebClient webClient, ServiceResolver serviceResolver) {
        this.webClient = webClient;
        this.serviceResolver = serviceResolver;
    }

    public Mono<UserDetailsDTO> getProfileByUser(String userId) {
        String baseUrl = serviceResolver.resolveBaseUrl("user-service");

        return webClient.get()
                .uri(baseUrl + "/users/{userId}/profile", userId)
                .retrieve()
                .bodyToMono(UserDetailsDTO.class);
    }


}
