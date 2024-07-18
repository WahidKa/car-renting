package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.BookingRepository;
import com.quartet.car_rental.dao.CarRepository;
import com.quartet.car_rental.dao.NotificationRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.*;
import com.quartet.car_rental.dto.Envelop.BookingEnvelop;
import com.quartet.car_rental.dto.Envelop.CarEnvelop;
import com.quartet.car_rental.dto.Envelop.UserEnvelop;
import com.quartet.car_rental.dto.request.BookingRequest;
import com.quartet.car_rental.dto.request.BookingUpdateRequest;
import com.quartet.car_rental.dto.request.PersonalInfo;
import com.quartet.car_rental.dto.response.BookingResponse;
import com.quartet.car_rental.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public BookingResponse createBooking(String email, BookingRequest request) {
        BookingResponse response = new BookingResponse();
        try {
            logger.info("### service - Create Booking - Begin ###");

            // Fetch user by email
            logger.info("Fetching user details for email: {}", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("User not found: {}", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            // Update user's personal information based on the request
            updateUserPersonalInfo(user, request.getPersonalInfo());

            // Fetch car by ID
            logger.info("Fetching car details for car ID: {}", request.getCarId());
            Optional<Car> carOptional = carRepository.findById(request.getCarId());
            if (!carOptional.isPresent()) {
                logger.info("Car not found: {}", request.getCarId());
                response.setStatus("404");
                response.setMessage("Car not found");
                return response;
            }

            Car car = carOptional.get();

            // Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setCar(car);
            booking.setStartDate(request.getStartDate());
            booking.setEndDate(request.getEndDate());
            booking.setStatus(BookingStatus.PENDING);

            // Save booking
            logger.info("Saving booking for user: {}", user.getEmail());
            bookingRepository.save(booking);

            // Create and save notification for the client
            Notification clientNotification = new Notification();
            clientNotification.setUser(user);
            clientNotification.setMessage("Your booking request has been sent successfully.");
            clientNotification.setTimestamp(new Date());
            clientNotification.setBooking(booking);
            notificationRepository.save(clientNotification);

            // Create and save notification for the agency
            User agencyUser = car.getAgency().getUsers().get(0);
            Notification agencyNotification = new Notification();
            agencyNotification.setUser(agencyUser);
            agencyNotification.setBooking(booking);
            agencyNotification.setMessage("A new booking request has been made for your car.");
            agencyNotification.setTimestamp(new Date());
            notificationRepository.save(agencyNotification);

            logger.info("### service - Create Booking - Booking created successfully by user {} ###", user.getFirstName());
            response.setStatus("200");
            response.setMessage("Booking created successfully");
        } catch (Exception e) {
            logger.error("### service - Create Booking - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }

    private void updateUserPersonalInfo(User user, PersonalInfo personalInfo) {
        user.setCin(personalInfo.getCin());
        user.setFirstName(personalInfo.getFirstName());
        user.setLastName(personalInfo.getLastName());
        user.setEmail(personalInfo.getEmail());
        user.setTel(personalInfo.getTel());
        user.setAddress(personalInfo.getAddress());
        userRepository.save(user);
    }

    @Override
    public BookingResponse updateBooking(String email, BookingUpdateRequest request) {
        BookingResponse response = new BookingResponse();
        try {
            logger.info("### service - Update Booking - Begin ###");

            // Fetch booking by ID
            logger.info("Fetching booking details for booking ID: {}", request.getBookingId());
            Optional<Booking> bookingOptional = bookingRepository.findById(request.getBookingId());
            if (!bookingOptional.isPresent()) {
                logger.info("Booking not found: {}", request.getBookingId());
                response.setStatus("404");
                response.setMessage("Booking not found");
                return response;
            }

            Booking booking = bookingOptional.get();

            // Fetch user by email
            logger.info("Fetching user details for email: {}", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("User not found: {}", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            boolean isClient = user.getRole().equals(UserRole.CLIENT);
            boolean isAgency = user.getRole().equals(UserRole.AGENCY);

            if (isClient && !booking.getUser().getEmail().equals(email)) {
                logger.warn("### service - Update Booking - User {} trying to update another user's booking ###", email);
                response.setStatus("403");
                response.setMessage("You can only update your own bookings");
                return response;
            }

            if (BookingStatus.PENDING.equals(booking.getStatus())) {
                if (isClient) {
                    booking.setStartDate(request.getStartDate());
                    booking.setEndDate(request.getEndDate());
                    createNotification(user, booking, "Your booking dates have been updated.");
                    createNotification(booking.getCar().getAgency().getUsers().get(0), booking, "A booking request has been updated.");
                } else if (isAgency) {
                    booking.setStatus(BookingStatus.valueOf(request.getStatus()));
                    createNotification(user, booking, "You have updated the booking status to " + request.getStatus() + ".");
                    createNotification(booking.getUser(), booking, "The status of your booking has been updated to " + request.getStatus() + ".");
                }
            } else if (BookingStatus.CONFIRMED.equals(booking.getStatus()) && isClient) {
                if (BookingStatus.COMPLETED.equals(request.getStatus())) {
                    booking.setStatus(BookingStatus.valueOf(request.getStatus()));
                    createNotification(user, booking, "You have marked your booking as completed.");
                    createNotification(booking.getCar().getAgency().getUsers().get(0), booking, "The client has marked the booking as completed.");
                } else {
                    response.setStatus("403");
                    response.setMessage("You can only mark a confirmed booking as completed");
                    return response;
                }
            } else {
                response.setStatus("403");
                response.setMessage("Booking cannot be updated in its current status");
                return response;
            }

            bookingRepository.save(booking);
            logger.info("### service - Update Booking - Booking updated successfully by user {} ###", email);
            response.setStatus("200");
            response.setMessage("Booking updated successfully");
        } catch (Exception e) {
            logger.error("### service - Update Booking - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }

    private void createNotification(User user, Booking booking, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setMessage(message);
        notification.setTimestamp(new Date());
        notification.setSeen(false);
        notificationRepository.save(notification);
    }

    @Override
    public BookingResponse cancelBooking(String email, Long id) {
        BookingResponse response = new BookingResponse();
        try {
            logger.info("### service - Cancel Booking - Begin ###");

            // Fetch booking by ID
            logger.info("Fetching booking details for booking ID: {}", id);
            Optional<Booking> bookingOptional = bookingRepository.findById(id);
            if (!bookingOptional.isPresent()) {
                logger.info("Booking not found: {}", id);
                response.setStatus("404");
                response.setMessage("Booking not found");
                return response;
            }

            Booking booking = bookingOptional.get();

            // Fetch user by email
            logger.info("Fetching user details for email: {}", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("User not found: {}", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            if (!booking.getUser().getEmail().equals(email)) {
                logger.warn("### service - Cancel Booking - User {} trying to cancel another user's booking ###", email);
                response.setStatus("403");
                response.setMessage("You can only cancel your own bookings");
                return response;
            }

            if (!BookingStatus.PENDING.equals(booking.getStatus())) {
                logger.warn("### service - Cancel Booking - Booking {} cannot be cancelled in its current status ###", id);
                response.setStatus("403");
                response.setMessage("Booking cannot be cancelled in its current status");
                return response;
            }

            booking.setStatus(BookingStatus.CANCELLED);

            bookingRepository.save(booking);

            // Create and save notification for the client
            createNotification(user, booking, "Your booking has been cancelled successfully.");

            // Create and save notification for the agency
            User agencyUser = booking.getCar().getAgency().getUsers().get(0);
            createNotification(agencyUser, booking, "A booking for your car has been cancelled.");

            logger.info("### service - Cancel Booking - Booking cancelled successfully by user {} ###", email);
            response.setStatus("200");
            response.setMessage("Booking cancelled successfully");
        } catch (Exception e) {
            logger.error("### service - Cancel Booking - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BookingResponse getBookingDetails(Long id, String email) {
        BookingResponse response = new BookingResponse();
        try {
            logger.info("### service - Get Booking Details - Begin ###");

            logger.info("Fetching user details for email: {}", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("User not found: {}", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User connectedUsed = userOptional.get();

            Optional<Booking> bookingOptional = bookingRepository.findById(id);
            if (!bookingOptional.isPresent()) {
                logger.info("Booking not found: {}", id);
                response.setStatus("404");
                response.setMessage("Booking not found");
                return response;
            }

            Booking booking = bookingOptional.get();
            BookingEnvelop bookingEnvelop = new BookingEnvelop();
            bookingEnvelop.setId(booking.getId());
            bookingEnvelop.setStatus(booking.getStatus().toString());
            bookingEnvelop.setStartDate(booking.getStartDate());
            bookingEnvelop.setEndDate(booking.getEndDate());
            bookingEnvelop.setCar(new CarEnvelop(booking.getCar()));

            if (UserRole.AGENCY.equals(connectedUsed.getRole())) {
                User user = booking.getUser();
                bookingEnvelop.setUser(new UserEnvelop(user));
            }

            response.setStatus("200");
            response.setMessage("Booking details retrieved successfully");
        } catch (Exception e) {
            logger.error("### service - Get Booking Details - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public BookingResponse getUserBookings(String email) {
        BookingResponse response = new BookingResponse();
        try {
            logger.info("### service - Get User Bookings - Begin ###");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("User not found"));

            List<Booking> bookings;

            if (user.getRole().equals(UserRole.CLIENT)) {
                logger.info("Fetching bookings for client: {}", email);
                bookings = bookingRepository.findByUserId(user.getId());
            } else if (user.getRole().equals(UserRole.AGENCY)) {
                logger.info("Fetching bookings for agency: {}", email);
                bookings = bookingRepository.findByCar_AgencyId(user.getAgency().getId());
            } else {
                logger.warn("User role not authorized to view bookings: {}", email);
                response.setStatus("403");
                response.setMessage("User role not authorized to view bookings");
                return response;
            }

            List<BookingEnvelop> envelopeBookings = bookings.stream()
                    .map(booking -> {
                        BookingEnvelop envelopeBooking = new BookingEnvelop();
                        envelopeBooking.setId(booking.getId());
                        envelopeBooking.setStatus(booking.getStatus().toString());
                        envelopeBooking.setStartDate(booking.getStartDate());
                        envelopeBooking.setEndDate(booking.getEndDate());
                        envelopeBooking.setCar(new CarEnvelop(booking.getCar()));
                        if (user.getRole().equals(UserRole.AGENCY)) {
                            envelopeBooking.setUser(new UserEnvelop(booking.getUser()));
                        }
                        return envelopeBooking;
                    })
                    .collect(Collectors.toList());

            response.setStatus("200");
            response.setMessage("Bookings retrieved successfully");
            response.setBookings(envelopeBookings);

            logger.info("### service - Get User Bookings - End ###");
        } catch (Exception e) {
            logger.error("### service - Get User Bookings - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }

}
