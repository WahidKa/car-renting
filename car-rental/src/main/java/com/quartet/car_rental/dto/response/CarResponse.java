package com.quartet.car_rental.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quartet.car_rental.dto.Envelop.CarEnvelop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarResponse {
    private CarEnvelop car;
    private String status;
    private String message;

    public CarResponse() {
    }
    public CarResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
