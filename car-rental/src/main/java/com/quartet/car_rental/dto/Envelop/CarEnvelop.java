package com.quartet.car_rental.dto.Envelop;

import lombok.Data;

import java.util.List;

@Data
public class CarEnvelop {
    private String id;
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
    private List<String> imageFileNames;
    private String status;
    private Boolean promotion;
    private float percentage;
}
