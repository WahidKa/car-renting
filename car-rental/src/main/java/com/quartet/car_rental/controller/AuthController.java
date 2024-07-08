package com.quartet.car_rental.controller;

import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.request.RegistrationRequest;
import com.quartet.car_rental.dto.request.UpdateRoleRequest;
import com.quartet.car_rental.dto.response.AuthResponse;
import com.quartet.car_rental.dto.response.LoginResponse;
import com.quartet.car_rental.dto.response.UpdateRoleResponse;
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
    public ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest request) {
        logger.info("### controller - Register User - Begin ###");
        AuthResponse response = authService.register(request);
        if ("200".equals(response.getStatus())) {
            logger.info("User {} registered successfully", request.getEmail());
            logger.info("### controller - Register User - End ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.error("Registration error: {}", response.getMessage());
            logger.info("### controller - Register User - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request) {
        logger.info("### controller - User Login - Begin ###");
        LoginResponse response = authService.login(request);
        if (!"200".equals(response.getStatus())) {
            logger.error("Login error: {}", response.getMessage());
            logger.info("### controller - User Login - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            logger.info("User {} logged in successfully", request.getEmail());
            logger.info("### controller - User Login - End ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestParam String grantType,
                                                        @RequestHeader Map<String, String> headers) throws Exception {
        logger.info("### controller - Generate Token - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String refreshToken = authorizationHeader.substring(7);
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


    @PatchMapping("/becomeAgency")
    public ResponseEntity<UpdateRoleResponse> updateRole(@RequestHeader Map<String, String> headers,
                                                         @RequestBody UpdateRoleRequest request) throws Exception {
        logger.info("### controller - Become Agency - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7);
        UpdateRoleResponse response = authService.updateRole(request, token);
        if (response == null) {
            logger.info("### controller - Become Agency - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            logger.info("You become an agency successfully");
            logger.info("### controller - Become Agency - End ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
