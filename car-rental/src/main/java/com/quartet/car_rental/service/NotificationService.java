package com.quartet.car_rental.service;

import com.quartet.car_rental.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(String email);
}
