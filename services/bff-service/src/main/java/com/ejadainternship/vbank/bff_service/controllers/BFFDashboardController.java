package com.ejadainternship.vbank.bff_service.controllers;

import com.ejadainternship.vbank.bff_service.dtos.DashboardDTO;
import com.ejadainternship.vbank.bff_service.services.BFFDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bff")
public class BFFDashboardController {
    public final BFFDashboardService bffDashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<DashboardDTO>> getDashboard(@PathVariable String userId) {
        return bffDashboardService.getDashboard(userId);
   }
}
