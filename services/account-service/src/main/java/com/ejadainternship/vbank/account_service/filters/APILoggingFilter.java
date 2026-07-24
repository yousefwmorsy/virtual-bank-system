package com.ejadainternship.vbank.account_service.filters;

import com.ejadainternship.vbank.account_service.dtos.LogItemDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class APILoggingFilter extends OncePerRequestFilter {
    @Autowired
    private KafkaTemplate<String, LogItemDTO> kafkaTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 0);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

        publish(requestBody, "Request");
        publish(responseBody, "Response");

        wrappedResponse.copyBodyToResponse();
    }

    private void publish(String body, String messageType) {
        LogItemDTO logItem = new LogItemDTO(body, messageType, LocalDateTime.now().toString());
        kafkaTemplate.send("microservices-logs", logItem);
    }
}
