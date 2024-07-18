package com.quartet.car_rental.dto.Envelop;

import com.quartet.car_rental.dao.entities.User;

public class UserEnvelop {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String tel;
    private String address;

    public UserEnvelop(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.address = user.getAddress();
    }
}
