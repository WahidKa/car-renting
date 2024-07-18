package com.quartet.car_rental.dao.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "agency")
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "city", length = 200)
    private String city;

    @Column(name = "fleetSize", length = 200)
    private String fleetSize;

    @Column(name = "latitude", length = 100)
    private Double latitude;

    @Column(name = "longitude", length = 100)
    private Double longitude;

    @OneToMany(mappedBy = "agency")
    private List<Car> cars;

    @OneToMany(mappedBy = "agency")
    private List<User> users;
}
