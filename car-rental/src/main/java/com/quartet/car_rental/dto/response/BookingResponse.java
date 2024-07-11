package com.quartet.car_rental.dto.response;

import com.quartet.car_rental.dao.entities.Booking;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BookingResponse {
    private String message;
    private Long bookingId;
    private String carMake;
    private String carModel;
    private Date startDate;
    private Date endDate;
    private String status;

    public BookingResponse(String message) {
        this.message = message;
    }

    public BookingResponse(String message, Booking booking) {
        this.message = message;
        this.bookingId = booking.getId();
        this.carMake = booking.getCar().getMake();
        this.carModel = booking.getCar().getModel();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
        this.status = booking.getStatus().toString();
    }
}
