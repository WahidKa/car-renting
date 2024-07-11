package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class SearchCriteria {
    private String make;
    private String model;
    private String type;
    private String transmissionType;
    private String fuelType;
    private Double minPrice;
}
