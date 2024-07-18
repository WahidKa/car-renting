package com.quartet.car_rental.dto.Envelop;

import lombok.Data;

import java.util.Date;

@Data
public class BookingEnvelop {
    private Long id;
    private String status;
    private Date startDate;
    private Date endDate;
    private CarEnvelop car;
    private UserEnvelop user;
}
