package com.quartet.car_rental.dto.Envelop;

import com.quartet.car_rental.dao.entities.Car;
import com.quartet.car_rental.dao.entities.CarImage;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

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

    public CarEnvelop(Car car) {
        this.id = String.valueOf(car.getId());
        this.make = car.getMake();
        this.model = car.getModel();
        this.fuelType = car.getFuelType();
        this.transmissionType = car.getTransmissionType();
        this.type = car.getType();
        this.matriculate = car.getMatriculate();
        this.seats = car.getSeats();
        this.year = car.getYear();
        this.description = car.getDescription();
        this.price = car.getPrice();
        this.imageFileNames = car.getImages().stream()
                .map(CarImage::getImagePath)
                .collect(Collectors.toList());
        this.status = String.valueOf(car.getStatus());
        this.promotion = car.getPromotion();
        this.percentage = car.getPercentage();
    }
}
