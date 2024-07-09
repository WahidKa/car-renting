package com.quartet.car_rental.dao;

import com.quartet.car_rental.dao.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
