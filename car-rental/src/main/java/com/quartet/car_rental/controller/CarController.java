package com.quartet.car_rental.controller;

import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarResponse;
import com.quartet.car_rental.helper.JwtUtil;
import com.quartet.car_rental.service.CarService;
import com.quartet.car_rental.token.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private static final Logger logger = LogManager.getLogger(CarController.class);

    @Autowired
    private CarService carService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<CarResponse> addCar(@RequestHeader Map<String, String> headers,
                                              @RequestBody CarRequest request) {
        String authorizationHeader = headers.get("authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header ");
            return new ResponseEntity<>(new CarResponse("Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String username = jwt.getSubject();
        try {
            CarResponse response = carService.addCar(username, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error adding car: {}", e.getMessage());
            return new ResponseEntity<>(new CarResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<CarResponse> updateCar(@PathVariable("id") Long id,
                                                 @RequestHeader Map<String, String> headers,
                                                 @RequestBody CarRequest request) {
        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarResponse("Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String username = jwt.getSubject();

        try {
            CarResponse response = carService.updateCar(username, id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            return new ResponseEntity<>(new CarResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<CarResponse> deleteCar(@PathVariable Long id,
                                                 @RequestHeader Map<String, String> headers,
                                                 @RequestParam(name = "quantity", defaultValue = "1") int quantity) {
        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarResponse("Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String username = jwt.getSubject();

        try {
            CarResponse response = carService.deleteCar(username, id, quantity);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting car: {}", e.getMessage());
            return new ResponseEntity<>(new CarResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarDetails(@PathVariable Long id) {
        try {
            CarResponse response = carService.getCarDetails(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching car details: {}", e.getMessage());
            return new ResponseEntity<>(new CarResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars(@RequestHeader Map<String, String> headers) {
        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String username = jwt.getSubject();

        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole().name().equals("AGENCY")) {
                List<CarResponse> cars = carService.getCarsByAgency(user.getAgency().getId());
                return new ResponseEntity<>(cars, HttpStatus.OK);
            } else {
                List<CarResponse> cars = carService.getCarsByLocation(user);
                return new ResponseEntity<>(cars, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error fetching cars: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<CarResponse>> searchCars(@RequestHeader Map<String, String> headers,
                                                        @RequestBody SearchCriteria criteria) {
        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String username = jwt.getSubject();

        try {
            List<CarResponse> cars = carService.searchCars(criteria);
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching cars: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
