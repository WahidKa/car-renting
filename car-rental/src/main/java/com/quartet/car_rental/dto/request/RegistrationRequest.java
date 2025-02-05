package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /*
    private String tel;
    private String location;
    private String role; // CLIENT or AGENCY
    private String agencyName; // Optional, used if role is AGENCY
    private String agencyAddress; // Optional, used if role is AGENCY
     */
}
