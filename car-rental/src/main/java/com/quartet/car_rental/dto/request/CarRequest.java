package com.quartet.car_rental.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CarRequest {
    private String make;
    private String model;
    private int year;
    private String status;
    private String color;
    private int quantity;
    private double price;
    private List<String> imageFileNames; // List of image file names
}
