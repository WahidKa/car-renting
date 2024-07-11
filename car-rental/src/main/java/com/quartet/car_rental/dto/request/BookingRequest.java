package com.quartet.car_rental.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class BookingRequest {
    private Long carId;
    private Date startDate;
    private Date endDate;
}
