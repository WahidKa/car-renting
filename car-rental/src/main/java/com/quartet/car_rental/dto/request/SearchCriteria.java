package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class SearchCriteria {
    private String make;
    private String model;
    private Integer year;
    private String color;
    private Double minPrice;
    private Double maxPrice;
    private String location;
}
