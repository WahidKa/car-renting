package com.quartet.car_rental.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class BookingUpdateRequest {
    private Long bookingId;
    private Date startDate;
    private Date endDate;
    private String status;
}
