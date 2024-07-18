package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class PersonalInfo {
    private String cin;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String address;
}
