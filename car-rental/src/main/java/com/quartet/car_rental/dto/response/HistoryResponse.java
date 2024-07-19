package com.quartet.car_rental.dto.response;

import com.quartet.car_rental.dto.Envelop.HistoryEnvelop;
import lombok.Data;

import java.util.List;

@Data
public class HistoryResponse {
    private String status;
    private String message;
    private List<HistoryEnvelop> bookings;
}
