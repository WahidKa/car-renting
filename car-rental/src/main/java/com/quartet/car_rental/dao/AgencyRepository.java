package com.quartet.car_rental.dao;

import com.quartet.car_rental.dao.entities.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository  extends JpaRepository<Agency, Long> {
}
