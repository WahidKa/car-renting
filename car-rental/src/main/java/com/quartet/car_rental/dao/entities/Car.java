package com.quartet.car_rental.dao.entities;

import lombok.Data;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;
}
