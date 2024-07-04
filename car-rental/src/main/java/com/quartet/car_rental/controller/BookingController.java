package com.quartet.car_rental.controller;

import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.response.BookingResponse;
import com.quartet.car_rental.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request,
                                                         @RequestHeader Map<String, String> headers) {
        try {
            String username = headers.get("username");
            BookingResponse response = bookingService.createBooking(username, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("Error creating booking"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable("id") Long id,
                                                         @RequestBody BookingRequest request,
                                                         @RequestHeader Map<String, String> headers) {
        try {
            String username = headers.get("username");
            BookingResponse response = bookingService.updateBooking(username, id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("Error updating booking"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable("id") Long id,
                                                         @RequestHeader Map<String, String> headers) {
        try {
            String username = headers.get("username");
            BookingResponse response = bookingService.cancelBooking(username, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error cancelling booking: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("Error cancelling booking"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable("id") Long id) {
        try {
            BookingResponse response = bookingService.getBookingDetails(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving booking details: {}", e.getMessage());
            return new ResponseEntity<>(new BookingResponse("Error retrieving booking details"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@RequestHeader Map<String, String> headers) {
        try {
            String username = headers.get("username");
            List<BookingResponse> responses = bookingService.getUserBookings(username);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving user bookings: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
