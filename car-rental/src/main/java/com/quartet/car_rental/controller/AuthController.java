package com.quartet.car_rental.controller;

import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.response.AuthResponse;
import com.quartet.car_rental.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request,
                                                 @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Register User - Begin ###");
        AuthResponse response = authService.register(request);
        if ("200".equals(response.getStatus())) {
            logger.info("User {} registered successfully", request.getUsername());
            logger.info("### controller - Register User - End ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.error("Registration error: {}", response.getErrors());
            logger.info("### controller - Register User - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request,
                                                     @RequestHeader Map<String, String> headers) {
        logger.info("### controller - User Login - Begin ###");
        Map<String, String> tokens = authService.login(request);
        if (tokens.containsKey("error")) {
            logger.error("Login error: {}", tokens.get("error"));
            logger.info("### controller - User Login - Error ###");
            return new ResponseEntity<>(tokens, HttpStatus.BAD_REQUEST);
        } else {
            logger.info("User {} logged in successfully", request.getUsername());
            logger.info("### controller - User Login - End ###");
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestParam String grantType,
                                                        @RequestParam(required = false) String refreshToken,
                                                        @RequestHeader Map<String, String> headers) throws Exception {
        logger.info("### controller - Generate Token - Begin ###");
        Map<String, String> tokens = authService.generateToken(grantType, null, null, refreshToken);
        if (tokens.containsKey("error")) {
            logger.error("Token generation error: {}", tokens.get("error"));
            logger.info("### controller - Generate Token - Error ###");
            return new ResponseEntity<>(tokens, HttpStatus.BAD_REQUEST);
        } else {
            logger.info("Token generated successfully");
            logger.info("### controller - Generate Token - End ###");
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        }
    }
}
