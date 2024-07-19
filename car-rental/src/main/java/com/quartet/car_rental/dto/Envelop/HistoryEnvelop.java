package com.quartet.car_rental.dto.Envelop;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryEnvelop {
    private String carImage;
    private String carModel;
    private double price;
    private int numberOfDaysBooked;
}
