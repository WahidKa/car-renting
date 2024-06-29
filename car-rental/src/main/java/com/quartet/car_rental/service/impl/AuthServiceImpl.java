package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.AgencyRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.Agency;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dao.entities.UserRole;
import com.quartet.car_rental.dto.request.AuthRequest;
import com.quartet.car_rental.dto.request.RegistrationRequest;
import com.quartet.car_rental.dto.response.AuthResponse;
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
        List<String> errors = new ArrayList<>();
        try {
            logger.info("### service - Register User - Begin ###");

            // Validate role
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(request.getRole().toUpperCase());
                if (!EnumSet.of(UserRole.AGENCY, UserRole.CLIENT).contains(userRole)) {
                    throw new IllegalArgumentException("Invalid role");
                }
            } catch (IllegalArgumentException e) {
                logger.warn("### service - Register User - Invalid role: {} ###", request.getRole());
                errors.add("Invalid role. Accepted values are: CLIENT, AGENCY");
                return new AuthResponse("400", errors);
            }

            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                logger.info("### service - Register User - Username {} already exists ###", request.getUsername());
                errors.add("Username already exists");
                return new AuthResponse("400", errors);
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(userRole);
            user.setLocation(request.getLocation());

            if (userRole == UserRole.AGENCY) {
                Agency agency = new Agency();
                agency.setName(request.getAgencyName());
                agency.setAddress(request.getAgencyAddress());
                agency = agencyRepository.save(agency);
                user.setAgency(agency);
            }

            userRepository.save(user);

            logger.info("### service - Register User - User {} registered successfully ###", request.getUsername());
            return new AuthResponse("200", "User registered successfully");
        } catch (Exception exp) {
            logger.error("### service - Register User - Technical error - End ###", exp);
            errors.add("Technical error");
            return new AuthResponse("500", errors);
        }
    }

    @Override
    public Map<String, String> login(AuthRequest request) {
        List<String> errors = new ArrayList<>();
        try {
            logger.info("### service - User Login - Begin ###");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != UserRole.AGENCY && user.getRole() != UserRole.CLIENT) {
                logger.warn("### service - User Login - User {} has invalid role: {} ###", request.getUsername(), user.getRole());
                errors.add("Invalid role");
                return Collections.singletonMap("error", "Invalid role");
            }

            logger.info("### service - User Login - User {} authenticated successfully ###", request.getUsername());
            Map<String, String> tokens = tokenService.generateToken("password", request.getUsername(), request.getPassword(), null);

            // Update user location during login
            user.setLocation(request.getLocation());
            userRepository.save(user);

            return tokenService.generateToken("password", request.getUsername(), request.getPassword(), null);
        } catch (Exception exp) {
            logger.error("### service - User Login - Technical error - End ###", exp);
            errors.add("Invalid username or password");
            return Collections.singletonMap("error", "Invalid username or password");
        }
    }

    @Override
    public Map<String, String> generateToken(String grantType, String username, String password, String refreshToken) throws Exception {
        return tokenService.generateToken(grantType, username, password, refreshToken);
    }
}
