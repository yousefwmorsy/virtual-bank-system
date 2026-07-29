package com.example.userservice.services;

import com.example.userservice.dtos.AuthResponse;
import com.example.userservice.models.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @Value("#{${wso2.client-mapping}}")
    private Map<String, String> azpMapping;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience:vbank-clients}")
    private String audience;

    @Value("${jwt.expiration-minutes:15}")
    private long expirationMinutes;

    public AuthResponse authenticate(String username, String password, String clientType) {
        String azp = azpMapping.get(clientType != null ? clientType.toLowerCase() : null);
        if (azp == null) {
            throw new RuntimeException("Invalid or missing X-Client-Type");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .audience(List.of(audience))
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMinutes * 60))
                .claim("userId", user.getId().toString())
                .claim("roles", user.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .claim("azp", azp)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(SignatureAlgorithm.RS256).build(),
                        claims
                )
        ).getTokenValue();

        return new AuthResponse(token, "Bearer", expirationMinutes * 60);
    }

}
