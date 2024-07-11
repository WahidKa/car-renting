package com.quartet.car_rental.service;

import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dto.Envelop.CarListEnvelop;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarListResponse;
import com.quartet.car_rental.dto.response.CarPatchResponse;
import com.quartet.car_rental.dto.response.CarResponse;

import java.util.List;

public interface CarService {
    CarPatchResponse addCar(String email, CarRequest request);
    CarPatchResponse updateCar(String username, Long id, CarRequest request) ;
    CarPatchResponse deleteCar(String username, Long id);
    CarResponse getCarDetails(Long id, User user);
    CarListResponse getCarsByAgency(Long agencyId);
    CarListResponse getCarsByLocation(User user);
    CarListResponse searchCars(SearchCriteria criteria);
}
