package com.quartet.car_rental.token.impl;

import com.quartet.car_rental.token.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LogManager.getLogger(TokenServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Map<String, String> generateToken(String grantType, String username, String password, String refreshToken) throws Exception {
        String subject;
        String scope;

        if ("password".equals(grantType)) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            subject = authentication.getName();
            scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
        } else if ("refreshToken".equals(grantType)) {
            if (refreshToken == null) {
                throw new Exception("Refresh Token is required");
            }
            Jwt decodedJwt = jwtDecoder.decode(refreshToken);
            subject = decodedJwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
        } else {
            throw new Exception("Unsupported grant type");
        }

        Map<String, String> tokens = new HashMap<>();
        Instant now = Instant.now();

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .issuer("car-rental-service")
                .claim("scope", scope)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        tokens.put("accessToken", accessToken);

        if ("password".equals(grantType)) {
            JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(now)
                    .expiresAt(now.plus(7, ChronoUnit.DAYS))
                    .issuer("car-rental-service")
                    .build();

            String refreshTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
            tokens.put("refreshToken", refreshTokenValue);
        }

        logger.info("### service - Generate Token - Tokens generated for user: {} ###", subject);
        return tokens;
    }
}
