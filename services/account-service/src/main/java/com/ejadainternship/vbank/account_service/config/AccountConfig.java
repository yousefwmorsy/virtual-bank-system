package com.ejadainternship.vbank.account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class AccountConfig {
    @Bean
    public SecureRandom random() {
        return new SecureRandom();
    }
}
