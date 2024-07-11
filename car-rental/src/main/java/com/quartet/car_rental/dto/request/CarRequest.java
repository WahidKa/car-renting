package com.quartet.car_rental.dto.request;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class CarRequest {
    private String make;
    private String model;
    private String fuelType;
    private String transmissionType;
    private String type;
    private String matriculate;
    private int seats;
    private int year;
    private String description;
    private double price;
    private List<String> imageFileNames; // List of image file names
    private String status;//update
    private Boolean promotion;//update
    private float percentage;//update
}
