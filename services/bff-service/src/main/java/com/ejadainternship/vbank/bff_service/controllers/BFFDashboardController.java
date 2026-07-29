package com.ejadainternship.vbank.bff_service.controllers;

import com.ejadainternship.vbank.bff_service.dtos.DashboardDTO;
import com.ejadainternship.vbank.bff_service.services.BFFDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bff")
public class BFFDashboardController {
    public final BFFDashboardService bffDashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<DashboardDTO>> getDashboard(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                           @AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        return bffDashboardService.getDashboard(userId, jwt, authorization);
    }
}
