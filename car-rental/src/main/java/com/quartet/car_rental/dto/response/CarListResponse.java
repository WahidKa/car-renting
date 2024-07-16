package com.quartet.car_rental.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quartet.car_rental.dto.Envelop.CarEnvelop;
import com.quartet.car_rental.dto.Envelop.CarListEnvelop;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarListResponse {
    private String status;
    private String message;
    private List<CarListEnvelop> carsByLocation;
    private List<CarListEnvelop> carsByClick;
    private List<CarListEnvelop> carsByAgency;
    private List<CarListEnvelop> carsWithPromotions;
    private List<CarListEnvelop> otherCars;

    public CarListResponse() {
    }

    public CarListResponse(String status, String message) {
        this.status = status;
        this.message= message;
    }
}
