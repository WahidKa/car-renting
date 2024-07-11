package com.quartet.car_rental.dto.Envelop;

import lombok.Data;
import java.util.List;

@Data
public class CarListEnvelop {
    private String id;
    private String make;
    private String model;
    private String type;
    private double price;
    private List<String> imageFileNames;
    private Boolean promotion;
    private float percentage;
}
