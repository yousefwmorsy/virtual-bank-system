package com.ejadainternship.vbank.bff_service.client;

import com.ejadainternship.vbank.bff_service.dtos.UserDetailsDTO;
import com.ejadainternship.vbank.bff_service.exceptions.DownstreamServiceException;
import com.ejadainternship.vbank.bff_service.exceptions.UserNotFoundException;
import com.ejadainternship.vbank.bff_service.utils.ServiceResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Component
public class UserServiceClient {
    private final WebClient webClient;
    private final ServiceResolver serviceResolver;

    public UserServiceClient(WebClient webClient, ServiceResolver serviceResolver) {
        this.webClient = webClient;
        this.serviceResolver = serviceResolver;
    }

    public Mono<UserDetailsDTO> getProfileByUser(String userId, String auth) {
        String baseUrl = serviceResolver.resolveBaseUrl("user-service");

        return webClient.get()
                .uri(baseUrl + "/users/{userId}/profile", userId)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new UserNotFoundException(userId))
                )
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(new DownstreamServiceException("Users Service"))
                )
                .bodyToMono(UserDetailsDTO.class)
                .timeout(Duration.ofSeconds(60));
    }


}
