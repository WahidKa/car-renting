package com.quartet.car_rental.controller;

import com.quartet.car_rental.dto.response.NotificationResponse;
import com.quartet.car_rental.helper.JwtUtil;
import com.quartet.car_rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT') or hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get User Notifications - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            List<NotificationResponse> notifications = notificationService.getUserNotifications(email);
            logger.info("### controller - Get User Notifications - Success ###");
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching notifications: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
