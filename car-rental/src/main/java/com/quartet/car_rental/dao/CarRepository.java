package com.quartet.car_rental.dao;

import com.quartet.car_rental.dao.entities.Agency;
import com.quartet.car_rental.dao.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    Optional<Car> findByMakeAndModelAndYearAndColorAndAgency(String make, String model, int year, String color, Agency agency);

    List<Car> findByAgencyId(Long agencyId);
}
