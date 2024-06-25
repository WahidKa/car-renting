package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
