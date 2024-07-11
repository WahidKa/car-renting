package com.quartet.car_rental.dao.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make", length = 50, nullable = false)
    private String make;

    @Column(name = "model", length = 50, nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status;

    @Column(name = "fuelType", length = 40)
    private String fuelType;

    @Column(name = "transmissionType", length = 40)
    private String transmissionType;

    @Column(name = "type", length = 40)
    private String type;

    @Column(name = "matriculate", length = 40)
    private String matriculate;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "seats", length = 40)
    private int seats;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "clicks", nullable = false)
    private double clicks = 0;

    @Column(name = "promotion")
    private Boolean promotion = false;

    @Column(name = "percentage")
    private float percentage;

    @ManyToOne
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarImage> images = new ArrayList<>();
}
