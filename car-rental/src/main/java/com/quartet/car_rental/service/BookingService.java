package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String username, BookingRequest request) throws Exception;
    BookingResponse updateBooking(String username, Long id, BookingRequest request) throws Exception;
    BookingResponse cancelBooking(String username, Long id) throws Exception;
    BookingResponse getBookingDetails(Long id) throws Exception;
    List<BookingResponse> getUserBookings(String username) throws Exception;
}
