package com.quartet.car_rental.helper;

import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtUtil {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    public Jwt validateToken(String token) throws JwtException {
        Jwt jwt = jwtDecoder.decode(token);
        if (jwt.getExpiresAt().isBefore(new Date().toInstant())) {
            throw new JwtException("Token is expired");
        }
        return jwt;
    }
}
