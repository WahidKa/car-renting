package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.BookingRepository;
import com.quartet.car_rental.dao.CarRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.Booking;
import com.quartet.car_rental.dao.entities.BookingStatus;
import com.quartet.car_rental.dao.entities.Car;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.response.BookingResponse;
import com.quartet.car_rental.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LogManager.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Override
    public BookingResponse createBooking(String username, BookingRequest request) throws Exception {
        logger.info("### service - Create Booking - Begin ###");

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("User not found"));

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new Exception("Car not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.valueOf("BOOKED"));

        bookingRepository.save(booking);

        logger.info("### service - Create Booking - Booking created successfully by user {} ###", username);
        return new BookingResponse("Booking created successfully", booking);
    }

    @Override
    public BookingResponse updateBooking(String username, Long id, BookingRequest request) throws Exception {
        logger.info("### service - Update Booking - Begin ###");

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new Exception("Booking not found"));

        if (!booking.getUser().getLastName().equals(username)) {
            throw new Exception("You can only update your own bookings");
        }

        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
/*
        booking.setStatus(request.getStatus());

 */
        bookingRepository.save(booking);

        logger.info("### service - Update Booking - Booking updated successfully by user {} ###", username);
        return new BookingResponse("Booking updated successfully", booking);
    }

    @Override
    public BookingResponse cancelBooking(String username, Long id) throws Exception {
        logger.info("### service - Cancel Booking - Begin ###");

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new Exception("Booking not found"));

        if (!booking.getUser().getEmail().equals(username)) {
            throw new Exception("You can only cancel your own bookings");
        }

        booking.setStatus(BookingStatus.valueOf("CANCELLED"));

        bookingRepository.save(booking);

        logger.info("### service - Cancel Booking - Booking cancelled successfully by user {} ###", username);
        return new BookingResponse("Booking cancelled successfully", booking);
    }

    @Override
    public BookingResponse getBookingDetails(Long id) throws Exception {
        logger.info("### service - Get Booking Details - Begin ###");

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new Exception("Booking not found"));

        return new BookingResponse("Booking details retrieved successfully", booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(String username) throws Exception {
        logger.info("### service - Get User Bookings - Begin ###");

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(booking -> new BookingResponse("Booking details retrieved successfully", booking))
                .collect(Collectors.toList());
    }
}
