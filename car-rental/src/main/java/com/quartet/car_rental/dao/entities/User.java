package com.quartet.car_rental.dao.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstname", length = 15, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 15, nullable = false)
    private String lastName;

    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "tel", length = 20, unique = true)
    private String tel;

    @Column(name = "job", length = 50)
    private String job;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @Column(name = "latitude", length = 100)
    private Double latitude;

    @Column(name = "longitude", length = 100)
    private Double longitude;
}
