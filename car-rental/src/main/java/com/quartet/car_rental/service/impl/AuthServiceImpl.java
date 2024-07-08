package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.AgencyRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dao.entities.UserRole;
import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.request.RegistrationRequest;
import com.quartet.car_rental.dto.response.AuthResponse;
import com.quartet.car_rental.dto.response.LoginResponse;
import com.quartet.car_rental.service.AuthService;
import com.quartet.car_rental.token.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public AuthResponse register(RegistrationRequest request) {
        try {
            logger.info("------ service - Register User - Begin ------");

            // Validate first name
            logger.info("------ Validating first name for user: {} ------", request.getFirstName());
            if (request.getFirstName() == null || !request.getFirstName().matches("^[a-zA-Z]{3,}$")) {
                logger.info("------ Invalid first name: {} ------", request.getFirstName());
                logger.info("------ service - Register User - End (Invalid first name) ------");
                return new AuthResponse("400", "First name must be more than 3 characters and contain only letters.");
            }
            logger.info("------ First name is valid for user: {} ------", request.getFirstName());

            // Validate last name
            logger.info("------ Validating last name for user: {} ------", request.getLastName());
            if (request.getLastName() == null || !request.getLastName().matches("^[a-zA-Z]{3,}$")) {
                logger.info("------ Invalid last name: {} ------", request.getLastName());
                logger.info("------ service - Register User - End (Invalid last name) ------");
                return new AuthResponse("400", "Last name must be more than 3 characters and contain only letters.");
            }
            logger.info("------ Last name is valid for user: {} ------", request.getLastName());

            // Validate email
            logger.info("------ Validating email for user: {} ------", request.getEmail());
            if (request.getEmail() == null || !request.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                logger.info("------ Invalid email: {} ------", request.getEmail());
                logger.info("------ service - Register User - End (Invalid email) ------");
                return new AuthResponse("400", "Invalid email format.");
            }
            logger.info("------ Email is valid for user: {} ------", request.getEmail());

            // Validate password
            logger.info("------ Validating password for user: {} ------", request.getEmail());
            if (request.getPassword() == null || request.getPassword().length() < 8) {
                logger.info("------ Invalid password for user: {} ------", request.getEmail());
                logger.info("------ service - Register User - End (Invalid password) ------");
                return new AuthResponse("400", "Password must be more than 8 characters.");
            }
            logger.info("------ Password is valid for user: {} ------", request.getEmail());

            // Check if the user already exists
            logger.info("------ Checking if user already exists: {} ------", request.getEmail());
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                logger.info("------ User {} already exists ------", request.getEmail());
                logger.info("------ service - Register User - End (User already exists) ------");
                return new AuthResponse("400", "Username already exists.");
            }

            // Register new user
            logger.info("------ Registering new user: {} ------", request.getEmail());
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(UserRole.CLIENT);

            userRepository.save(user);

            logger.info("------ User {} registered successfully ------", request.getEmail());
            logger.info("------ service - Register User - End (Success) ------");
            return new AuthResponse("200", "User registered successfully.");
        } catch (Exception exp) {
            logger.error("------ service - Register User - Technical error - End ------", exp);
            return new AuthResponse("500", "Technical error: " + exp.getMessage());
        }
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        try {
            logger.info("------ service - User Login - Begin ------");

            // Validate email format
            logger.info("------ Validating email for user : {} ------", request.getEmail());
            if (request.getEmail() == null || !request.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                logger.info("------ Invalid email : {} ------", request.getEmail());
                logger.info("------ service - Register User - End ( Invalid email ) ------");
                return new LoginResponse("400", "Invalid email format.", null, null);
            }
            logger.info("------ Email is valid for user : {} ------", request.getEmail());

            // Authenticate user
            logger.info("------ Authenticating user: {} ------", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Fetch user details
            logger.info("------ Fetching user details for user: {} ------", request.getEmail());
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        logger.info("------ Authentication failed: User not found for user: {} ------", request.getEmail());
                        logger.info("------ service - User Login - End (User not found) ------");
                        return new RuntimeException("User not found");
                    });

            // Validate user role
            logger.info("------ Validating role for user: {} ------", request.getEmail());
            if (user.getRole() != UserRole.AGENCY && user.getRole() != UserRole.CLIENT && user.getRole() != UserRole.ADMIN) {
                logger.warn("------ Authentication failed: User {} has invalid role: {} ------", request.getEmail(), user.getRole());
                logger.info("------ service - User Login - End (Invalid role) ------");
                return new LoginResponse("400", "Invalid role", null, null);
            }

            // Generate tokens
            logger.info("------ Generating tokens for user: {} ------", request.getEmail());
            Map<String, String> tokens = generateToken("password", request.getEmail(), request.getPassword(), null);
            if (tokens == null) {
                logger.info("------ Authentication failed: Error During Token Generation: {} ------", request.getEmail());
                logger.info("------ service - User Login - End (Error During Token Generation) ------");
                return new LoginResponse("500", "Error During Token Generation", null, null);
            }

            // Save user's location
            user.setLatitude(request.getLatitude());
            user.setLongitude(request.getLongitude());
            logger.info("------ Updating location for user: {} ------", request.getEmail());
            userRepository.save(user);

            logger.info("------ User {} authenticated successfully ------", request.getEmail());
            logger.info("------ service - User Login - End (Success) ------");
            return new LoginResponse("200", "User authenticated successfully", tokens.get("accessToken"), tokens.get("refreshToken"));
        } catch (Exception exp) {
            logger.error("------ service - User Login - Technical error - End ------", exp);
            return new LoginResponse("500", "Technical error: " + exp.getMessage(), null, null);
        }
    }

    @Override
    public Map<String, String> generateToken(String grantType, String email, String password, String refreshToken) throws Exception {
        return tokenService.generateToken(grantType, email, password, refreshToken);
    }
}
