package com.quartet.car_rental.service;

import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {
    CarResponse addCar(String username, CarRequest request) throws Exception;
    CarResponse updateCar(String username, Long id, CarRequest request) throws Exception;
    CarResponse deleteCar(String username, Long id, int quantity) throws Exception;
    CarResponse getCarDetails(Long id) throws Exception;
    List<CarResponse> getCarsByAgency(Long agencyId);
    List<CarResponse> getCarsByLocation(User user);
    List<CarResponse> searchCars(SearchCriteria criteria);
}
