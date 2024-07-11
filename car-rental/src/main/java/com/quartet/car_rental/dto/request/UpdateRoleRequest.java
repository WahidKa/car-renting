package com.quartet.car_rental.dto.request;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String companyName;
    private String jobTitle;
    private String fleetSize;
    private String contactNumber;
    private String city;
    private String address;
    private double latitude;
    private double longitude;
}
