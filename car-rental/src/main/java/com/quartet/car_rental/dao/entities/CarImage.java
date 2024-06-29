package com.quartet.car_rental.dao.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "car_image")
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "image_path", nullable = false)
    private String imagePath;
}
