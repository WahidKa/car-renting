package com.quartet.car_rental.dao;

import com.quartet.car_rental.dao.entities.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {
}
