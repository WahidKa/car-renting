package com.quartet.car_rental.dto.response;

import com.quartet.car_rental.dto.Envelop.BookingEnvelop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String message;
    private String status;
    private List<BookingEnvelop> bookings;

    public BookingResponse(String message) {
        this.message = message;
    }

    public BookingResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
