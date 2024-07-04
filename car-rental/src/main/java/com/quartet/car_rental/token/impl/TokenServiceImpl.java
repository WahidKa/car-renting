package com.quartet.car_rental.token.impl;

import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.token.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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
    private UserRepository userRepository;

    @Override
    public Map<String, String> generateToken(String grantType, String email, String password, String refreshToken) throws Exception {
        String subject;
        String scope;

        if ("password".equals(grantType)) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            subject = authentication.getName();
        } else if ("refreshToken".equals(grantType)) {
            if (refreshToken == null) {
                return null;
            }
            Jwt decodedJwt = jwtDecoder.decode(refreshToken);
            subject = decodedJwt.getSubject();
        } else {
            return null;
        }

        // Fetch the user from the repository to get the role
        User user = userRepository.findByEmail(subject).orElseThrow(() -> new Exception("User not found"));
        scope = user.getRole().name();

        Map<String, String> tokens = new HashMap<>();
        Instant now = Instant.now();

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .issuer("car-rental-service")
                .claim("scope", scope)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        tokens.put("accessToken", accessToken);

        if ("password".equals(grantType)) {
            JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(now)
                    .expiresAt(now.plus(30, ChronoUnit.DAYS))
                    .build();

            String refreshTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
            tokens.put("refreshToken", refreshTokenValue);
        }

        logger.info("### service - Generate Token - Tokens generated for user: {} ###", subject);
        return tokens;
    }

}
