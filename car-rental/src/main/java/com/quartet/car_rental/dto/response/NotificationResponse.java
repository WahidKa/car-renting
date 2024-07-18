package com.quartet.car_rental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private Date timestamp;
    private boolean seen;
    private Long bookingId;
    private String bookingStatus;

    // Constructor for non-booking related notifications
    public NotificationResponse(Long id, String message, Date timestamp, boolean seen) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }
}
