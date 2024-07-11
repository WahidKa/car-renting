package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private double latitude;
    private double longitude;
}
