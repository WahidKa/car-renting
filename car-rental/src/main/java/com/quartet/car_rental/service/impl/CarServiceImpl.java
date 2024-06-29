package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.entities.CarImage;
import com.quartet.car_rental.dao.entities.CarStatus;
import com.quartet.car_rental.dao.specification.CarSpecification;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarResponse;
import com.quartet.car_rental.dao.AgencyRepository;
import com.quartet.car_rental.dao.CarImageRepository;
import com.quartet.car_rental.dao.CarRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.dao.entities.Agency;
import com.quartet.car_rental.dao.entities.Car;
import com.quartet.car_rental.dao.entities.User;
import com.quartet.car_rental.service.CarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LogManager.getLogger(CarServiceImpl.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private CarImageRepository carImageRepository;

    private static final String IMAGE_DIR = "images/cars/";

    @Override
    public CarResponse addCar(String username, CarRequest request) throws Exception {
        logger.info("### service - Add Car - Begin ###");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getRole().name().equals("AGENCY")) {
            logger.info("### service - Add Car - User {} is not an agency ###", username);
            throw new Exception("Only agencies can add cars");
        }

        CarStatus carStatus;
        try {
            carStatus = CarStatus.valueOf(request.getStatus().toUpperCase());
            if (!EnumSet.allOf(CarStatus.class).contains(carStatus)) {
                throw new IllegalArgumentException("Invalid car status");
            }
        } catch (IllegalArgumentException e) {
            logger.info("### service - Add Car - Invalid car status: {} ###", request.getStatus());
            throw new Exception("Invalid car status. Accepted values are: " + EnumSet.allOf(CarStatus.class));
        }

        Agency agency = user.getAgency();

        Optional<Car> existingCar = carRepository.findByMakeAndModelAndYearAndColorAndAgency(
                request.getMake(), request.getModel(), request.getYear(), request.getColor(), agency);

        Car car;
        if (existingCar.isPresent()) {
            car = existingCar.get();
            car.setQuantity(car.getQuantity() + request.getQuantity());
            logger.info("### service - Add Car - Incremented quantity of existing car by user {} ###", username);
        } else {
            car = new Car();
            car.setMake(request.getMake());
            car.setModel(request.getModel());
            car.setYear(request.getYear());
            car.setStatus(carStatus);
            car.setColor(request.getColor());
            car.setQuantity(request.getQuantity());
            car.setPrice(request.getPrice());
            car.setAgency(agency);
            logger.info("### service - Add Car - Created new car entry by user {} ###", username);
        }

        carRepository.save(car);
        saveCarImages(car, request.getImageFileNames());

        logger.info("### service - Add Car - Car added/updated successfully by user {} ###", username);
        return new CarResponse("Car added/updated successfully");
    }

    @Override
    public CarResponse updateCar(String username, Long id, CarRequest request) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getRole().name().equals("AGENCY")) {
            throw new Exception("Only agencies can update cars");
        }

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new Exception("Car not found"));

        if (!car.getAgency().getId().equals(user.getAgency().getId())) {
            throw new Exception("You can only update cars from your agency");
        }

        CarStatus carStatus;
        try {
            carStatus = CarStatus.valueOf(request.getStatus().toUpperCase());
            if (!EnumSet.allOf(CarStatus.class).contains(carStatus)) {
                throw new IllegalArgumentException("Invalid car status");
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid car status. Accepted values are: " + EnumSet.allOf(CarStatus.class));
        }

        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setStatus(carStatus);
        car.setColor(request.getColor());
        car.setQuantity(request.getQuantity());
        car.setPrice(request.getPrice());

        carRepository.save(car);
        saveCarImages(car, request.getImageFileNames());

        return new CarResponse("Car updated successfully");
    }

    @Override
    public CarResponse deleteCar(String username, Long id, int quantity) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getRole().name().equals("AGENCY")) {
            throw new Exception("Only agencies can delete cars");
        }

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new Exception("Car not found"));

        if (!car.getAgency().getId().equals(user.getAgency().getId())) {
            throw new Exception("You can only delete cars from your agency");
        }

        if (car.getQuantity() > quantity) {
            car.setQuantity(car.getQuantity() - quantity);
            carRepository.save(car);
        } else if (car.getQuantity() == quantity) {
            carImageRepository.deleteAll(car.getImages());
            carRepository.delete(car);
        } else {
            throw new Exception("Cannot delete more cars than available in stock");
        }

        return new CarResponse("Car(s) deleted successfully");
    }

    private void saveCarImages(Car car, List<String> imageFileNames) {
        for (String fileName : imageFileNames) {
            String imagePath = Paths.get(IMAGE_DIR, fileName).toString();

            CarImage carImage = new CarImage();
            carImage.setCar(car);
            carImage.setImagePath(imagePath);
            carImageRepository.save(carImage);
        }
    }

    @Override
    public CarResponse getCarDetails(Long id) throws Exception {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new Exception("Car not found"));

        List<String> imagePaths = car.getImages().stream()
                .map(CarImage::getImagePath)
                .collect(Collectors.toList());

        return new CarResponse(car.getMake(), car.getModel(), car.getYear(), car.getStatus(), car.getColor(), car.getQuantity(), car.getPrice(), imagePaths);
    }

    @Override
    public List<CarResponse> getCarsByAgency(Long agencyId) {
        logger.info("### service - Get Cars By Agency - Begin ###");
        return carRepository.findByAgencyId(agencyId).stream()
                .map(car -> new CarResponse(car.getMake(), car.getModel(), car.getYear(), car.getStatus(), car.getColor(), car.getQuantity(), car.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarResponse> getCarsByLocation(User user) {
        logger.info("### service - Get Cars By Location - Begin ###");
        return carRepository.findAll().stream()
                .filter(car -> car.getAgency().getAddress().contains(user.getLocation()))
                .map(car -> new CarResponse(car.getMake(), car.getModel(), car.getYear(), car.getStatus(), car.getColor(), car.getQuantity(), car.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarResponse> searchCars(SearchCriteria criteria) {
        logger.info("### service - Search Cars - Begin ###");
        Specification<Car> specification = CarSpecification.search(criteria);
        return carRepository.findAll(specification).stream()
                .map(car -> new CarResponse(
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getStatus(),
                        car.getColor(),
                        car.getQuantity(),
                        car.getPrice()
                )).collect(Collectors.toList());
    }
}
