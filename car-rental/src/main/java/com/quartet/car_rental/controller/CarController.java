package com.quartet.car_rental.controller;

import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dao.entities.UserRole;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarListResponse;
import com.quartet.car_rental.dto.response.CarPatchResponse;
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
    public ResponseEntity<CarPatchResponse> addCar(@RequestHeader Map<String, String> headers,
                                                    @RequestBody CarRequest request) {
        logger.info("### controller - Add Car - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarPatchResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7);
        Jwt jwt = jwtUtil.validateToken(token);
        String email = jwt.getSubject();
        CarPatchResponse response = carService.addCar(email, request);
        if ("200".equals(response.getStatus())) {
            logger.info("### controller - Add Car - Success ###");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            logger.error("Error adding car: {}", response.getMessage());
            logger.info("### controller - Add Car - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<CarPatchResponse> updateCar(@PathVariable("id") Long id,
                                                      @RequestHeader Map<String, String> headers,
                                                      @RequestBody CarRequest request) {
        logger.info("### controller - Update Car - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarPatchResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7);
        Jwt jwt = jwtUtil.validateToken(token);
        String email = jwt.getSubject();
        CarPatchResponse response = carService.updateCar(email, id, request);
        if ("200".equals(response.getStatus())) {
            logger.info("### controller - Update Car - Success ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.error("Error updating car: {}", response.getMessage());
            logger.info("### controller - Update Car - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<CarPatchResponse> deleteCar(@PathVariable Long id,
                                                      @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Delete Car - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarPatchResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        CarPatchResponse response = carService.deleteCar(email, id);
        if ("200".equals(response.getStatus())) {
            logger.info("### controller - Delete Car - Success ###");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.error("Error deleting car: {}", response.getMessage());
            logger.info("### controller - Delete Car - Error ###");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarDetails(@PathVariable Long id,
                                                     @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get Car Details - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CarResponse response = carService.getCarDetails(id, user);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Get Car Details - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.error("Error fetching car details: {}", response.getMessage());
                logger.info("### controller - Get Car Details - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error fetching car details: {}", e.getMessage());
            return new ResponseEntity<>(new CarResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<CarListResponse> getAllCars(@RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get All Cars - Begin ###");
        CarListResponse response = new CarListResponse();

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            response.setStatus("403");
            response.setMessage("Missing or invalid Authorization header");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole().equals(UserRole.AGENCY)) {
                response = carService.getCarsByAgency(user.getAgency().getId());
            } else {
                response = carService.getCarsByLocation(user);
            }

            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Get All Cars - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.error("Error fetching cars: {}", response.getMessage());
                logger.info("### controller - Get All Cars - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error fetching cars: {}", e.getMessage());
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<CarListResponse> searchCars(@RequestHeader Map<String, String> headers,
                                                      @RequestBody SearchCriteria criteria) {
        logger.info("### controller - Search Cars - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new CarListResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token

        try {
            CarListResponse response = carService.searchCars(criteria);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Search Cars - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.info("### controller - Search Cars - No cars found ###");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error fetching cars: {}", e.getMessage());
            return new ResponseEntity<>(new CarListResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
