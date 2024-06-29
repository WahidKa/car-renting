package com.quartet.car_rental.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quartet.car_rental.dao.entities.CarStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarResponse {
    private String make;
    private String model;
    private Integer year;
    private String status;
    private String color;
    private Integer quantity;
    private Double price;
    private String message;
    private List<String> imagePaths;

    // Constructor for message only
    public CarResponse(String message) {
        this.message = message;
    }

    // Constructor for basic car details
    public CarResponse(String make, String model, int year, String status) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.status = status;
    }

    // Constructor for detailed car information without images
    public CarResponse(String make, String model, int year, CarStatus status, String color, int quantity, double price) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.status = status.toString();
        this.color = color;
        this.quantity = quantity;
        this.price = price;
    }

    // Constructor for detailed car information with images
    public CarResponse(String make, String model, int year, CarStatus status, String color, int quantity, double price, List<String> imagePaths) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.status = status.toString();
        this.color = color;
        this.quantity = quantity;
        this.price = price;
        this.imagePaths = imagePaths;
    }
}
