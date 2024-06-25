package com.quartet.car_rental.dto.request;

import com.quartet.car_rental.dao.entities.UserRole;
import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private UserRole role; // CLIENT or AGENCY
    private String agencyName; // Optional, used if role is AGENCY
    private String agencyAddress; // Optional, used if role is AGENCY
}
