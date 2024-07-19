package com.quartet.car_rental.controller;

import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.request.BookingUpdateRequest;
import com.quartet.car_rental.dto.response.BookingResponse;
import com.quartet.car_rental.dto.response.HistoryResponse;
import com.quartet.car_rental.helper.JwtUtil;
import com.quartet.car_rental.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LogManager.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<BookingResponse> createBooking(@RequestHeader Map<String, String> headers,
                                                         @RequestBody BookingRequest request) {
        logger.info("### controller - Create Booking - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new BookingResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            BookingResponse response = bookingService.createBooking(email, request);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Create Booking - Success ###");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                logger.error("Error creating booking: {}", response.getMessage());
                logger.info("### controller - Create Booking - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT') or hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<BookingResponse> updateBooking(@RequestBody BookingUpdateRequest request,
                                                         @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Update Booking - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new BookingResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            BookingResponse response = bookingService.updateBooking(email, request);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Update Booking - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("403".equals(response.getStatus())) {
                logger.info("### controller - Update Booking - Forbidden ###");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            } else if ("404".equals(response.getStatus())) {
                logger.info("### controller - Update Booking - Not Found ###");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.error("Error updating booking: {}", response.getMessage());
                logger.info("### controller - Update Booking - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable("id") Long id,
                                                         @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Cancel Booking - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new BookingResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            BookingResponse response = bookingService.cancelBooking(email, id);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Cancel Booking - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("403".equals(response.getStatus())) {
                logger.info("### controller - Cancel Booking - Forbidden ###");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            } else if ("404".equals(response.getStatus())) {
                logger.info("### controller - Cancel Booking - Not Found ###");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.error("Error cancelling booking: {}", response.getMessage());
                logger.info("### controller - Cancel Booking - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error cancelling booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT') or hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable("id") Long id,
                                                             @RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get Booking Details - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new BookingResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            BookingResponse response = bookingService.getBookingDetails(id, email);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Get Booking Details - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("403".equals(response.getStatus())) {
                logger.info("### controller - Get Booking Details - Forbidden ###");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            } else if ("404".equals(response.getStatus())) {
                logger.info("### controller - Get Booking Details - Not Found ###");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.error("Error retrieving booking details: {}", response.getMessage());
                logger.info("### controller - Get Booking Details - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error retrieving booking details: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT') or hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<BookingResponse> getUserBookings(@RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get User Bookings - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(new BookingResponse("403", "Missing or invalid Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            BookingResponse response = bookingService.getUserBookings(email);
            if ("200".equals(response.getStatus())) {
                logger.info("### controller - Get User Bookings - Success ###");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("403".equals(response.getStatus())) {
                logger.info("### controller - Get User Bookings - Forbidden ###");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            } else if ("404".equals(response.getStatus())) {
                logger.info("### controller - Get User Bookings - Not Found ###");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.error("Error retrieving user bookings: {}", response.getMessage());
                logger.info("### controller - Get User Bookings - Error ###");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error retrieving user bookings: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("500", "Technical error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rides/history")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT') or hasAuthority('SCOPE_AGENCY')")
    public ResponseEntity<HistoryResponse> getRideHistory(@RequestHeader Map<String, String> headers) {
        logger.info("### controller - Get Ride History - Begin ###");

        String authorizationHeader = headers.get("authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Missing or invalid Authorization header");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Jwt jwt = jwtUtil.validateToken(token); // Validate the access token
        String email = jwt.getSubject();

        try {
            HistoryResponse rideHistory = bookingService.getRideHistory(email);
            logger.info("### controller - Get Ride History - Success ###");
            return new ResponseEntity<>(rideHistory, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching ride history: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
