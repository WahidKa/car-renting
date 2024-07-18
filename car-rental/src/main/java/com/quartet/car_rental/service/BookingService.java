package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.request.BookingUpdateRequest;
import com.quartet.car_rental.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String email, BookingRequest request);
    BookingResponse updateBooking(String email, BookingUpdateRequest request) ;
    BookingResponse cancelBooking(String email, Long id);
    BookingResponse getBookingDetails(Long id, String email);
    BookingResponse getUserBookings(String email);
}
