package com.quartet.car_rental.service.impl;

import com.quartet.car_rental.dao.entities.*;
import com.quartet.car_rental.dao.specification.CarSpecification;
import com.quartet.car_rental.dto.Envelop.CarEnvelop;
import com.quartet.car_rental.dto.Envelop.CarListEnvelop;
import com.quartet.car_rental.dto.request.CarRequest;
import com.quartet.car_rental.dto.request.SearchCriteria;
import com.quartet.car_rental.dto.response.CarListResponse;
import com.quartet.car_rental.dto.response.CarPatchResponse;
import com.quartet.car_rental.dto.response.CarResponse;
import com.quartet.car_rental.dao.AgencyRepository;
import com.quartet.car_rental.dao.CarImageRepository;
import com.quartet.car_rental.dao.CarRepository;
import com.quartet.car_rental.dao.UserRepository;
import com.quartet.car_rental.service.CarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Comparator;
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
    public CarPatchResponse addCar(String email, CarRequest request) {
        CarPatchResponse response = new CarPatchResponse();
        try {
            logger.info("### service - Add Car - Begin ###");

            // Find user by email
            logger.info("### service - Add Car - Fetching user details for user: {} ###", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("### service - Add Car - User not found: {} ###", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            // Validate user role
            logger.info("### service - Add Car - Validating role for user: {} ###", email);
            if (!user.getRole().equals(UserRole.AGENCY)) {
                logger.warn("### service - Add Car - User {} is not an agency ###", email);
                response.setStatus("400");
                response.setMessage("Only agencies can add cars");
                return response;
            }

            // Create and save the car
            Agency agency = user.getAgency();
            Car car = setCar(request, agency);
            logger.info("### service - Add Car - Created new car entry by user {} ###", email);

            carRepository.save(car);
            saveCarImages(car, request.getImageFileNames());

            logger.info("### service - Add Car - Car added/updated successfully by user {} ###", email);
            response.setStatus("200");
            response.setMessage("Car added/updated successfully");
            return response;
        } catch (Exception e) {
            logger.error("### service - Add Car - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
    }
    private static Car setCar(CarRequest request, Agency agency) {
        Car car = new Car();
        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setFuelType(request.getFuelType());
        car.setTransmissionType(request.getTransmissionType());
        car.setType(request.getType());
        car.setMatriculate(request.getMatriculate());
        car.setSeats(request.getSeats());
        car.setYear(request.getYear());
        car.setStatus(CarStatus.AVAILABLE);
        car.setDescription(request.getDescription());
        car.setPrice(request.getPrice());
        car.setAgency(agency);
        return car;
    }

    @Override
    public CarPatchResponse updateCar(String email, Long id, CarRequest request) {
        CarPatchResponse response = new CarPatchResponse();
        try {
            logger.info("### service - Update Car - Begin ###");

            // Find user by email
            logger.info("### service - Update Car - Fetching user details for user: {} ###", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("### service - Update Car - User not found: {} ###", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            // Validate user role
            logger.info("### service - Update Car - Validating role for user: {} ###", email);
            if (!user.getRole().equals(UserRole.AGENCY)) {
                logger.warn("### service - Update Car - User {} is not an agency ###", email);
                response.setStatus("400");
                response.setMessage("Only agencies can update cars");
                return response;
            }

            // Find car by id
            logger.info("### service - Update Car - Fetching car details for car ID: {} ###", id);
            Optional<Car> carOptional = carRepository.findById(id);
            if (!carOptional.isPresent()) {
                logger.info("### service - Update Car - Car not found: {} ###", id);
                response.setStatus("404");
                response.setMessage("Car not found");
                return response;
            }

            Car car = carOptional.get();

            // Validate that the car belongs to the user's agency
            if (!car.getAgency().getId().equals(user.getAgency().getId())) {
                logger.warn("### service - Update Car - Car does not belong to user's agency: {} ###", id);
                response.setStatus("403");
                response.setMessage("You can only update cars from your agency");
                return response;
            }

            // Update car details
            logger.info("### service - Update Car - Updating car details for car ID: {} ###", id);
            CarStatus carStatus;
            try {
                carStatus = CarStatus.valueOf(request.getStatus().toUpperCase());
                if (!EnumSet.allOf(CarStatus.class).contains(carStatus)) {
                    logger.info("### service - Update Car - Invalid car status: {} ###", request.getStatus());
                    response.setStatus("400");
                    response.setMessage("Invalid car status. Accepted values are: " + EnumSet.allOf(CarStatus.class));
                    return response;
                }
            } catch (IllegalArgumentException e) {
                logger.info("### service - Update Car - Invalid car status: {} ###", request.getStatus());
                response.setStatus("400");
                response.setMessage("Invalid car status. Accepted values are: " + EnumSet.allOf(CarStatus.class));
                return response;
            }

            car.setMake(request.getMake());
            car.setModel(request.getModel());
            car.setFuelType(request.getFuelType());
            car.setTransmissionType(request.getTransmissionType());
            car.setType(request.getType());
            car.setMatriculate(request.getMatriculate());
            car.setSeats(request.getSeats());
            car.setYear(request.getYear());
            car.setStatus(carStatus);
            car.setDescription(request.getDescription());
            car.setPrice(request.getPrice());
            car.setPromotion(request.getPromotion());
            car.setPercentage(request.getPercentage());

            carRepository.save(car);
            saveCarImages(car, request.getImageFileNames());

            logger.info("### service - Update Car - Car updated successfully for car ID: {} ###", id);
            response.setStatus("200");
            response.setMessage("Car updated successfully");
            return response;
        } catch (Exception e) {
            logger.error("### service - Update Car - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CarPatchResponse deleteCar(String email, Long id) {
        CarPatchResponse response = new CarPatchResponse();
        try {
            logger.info("### service - Delete Car - Begin ###");

            // Find user by email
            logger.info("### service - Delete Car - Fetching user details for user: {} ###", email);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                logger.info("### service - Delete Car - User not found: {} ###", email);
                response.setStatus("404");
                response.setMessage("User not found");
                return response;
            }

            User user = userOptional.get();

            // Validate user role
            logger.info("### service - Delete Car - Validating role for user: {} ###", email);
            if (!user.getRole().equals(UserRole.AGENCY)) {
                logger.warn("### service - Delete Car - User {} is not an agency ###", email);
                response.setStatus("400");
                response.setMessage("Only agencies can delete cars");
                return response;
            }

            // Find car by id
            logger.info("### service - Delete Car - Fetching car details for car ID: {} ###", id);
            Optional<Car> carOptional = carRepository.findById(id);
            if (!carOptional.isPresent()) {
                logger.info("### service - Delete Car - Car not found: {} ###", id);
                response.setStatus("404");
                response.setMessage("Car not found");
                return response;
            }

            Car car = carOptional.get();

            // Validate that the car belongs to the user's agency
            if (!car.getAgency().getId().equals(user.getAgency().getId())) {
                logger.warn("### service - Delete Car - Car does not belong to user's agency: {} ###", id);
                response.setStatus("403");
                response.setMessage("You can only delete cars from your agency");
                return response;
            }

            // Delete car images and car
            carImageRepository.deleteAll(car.getImages());
            carRepository.delete(car);

            logger.info("### service - Delete Car - Car deleted successfully for car ID: {} ###", id);
            response.setStatus("200");
            response.setMessage("Car deleted successfully");
            return response;
        } catch (Exception e) {
            logger.error("### service - Delete Car - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
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
    public CarResponse getCarDetails(Long id, User user) {
        CarResponse response = new CarResponse();
        try {
            logger.info("### service - Get Car Details - Begin ###");

            // Find car by id
            logger.info("### service - Get Car Details - Fetching car details for car ID: {} ###", id);
            Optional<Car> carOptional = carRepository.findById(id);
            if (!carOptional.isPresent()) {
                logger.info("### service - Get Car Details - Car not found: {} ###", id);
                response.setStatus("404");
                response.setMessage("Car not found");
                return response;
            }

            Car car = carOptional.get();
            CarEnvelop carEnvelop = new CarEnvelop(car);
            if (user.getRole().equals(UserRole.CLIENT)) {
                car.setClicks(car.getClicks()+1);
                carRepository.save(car);
            }
            response.setCar(carEnvelop);
            response.setStatus("200");
            response.setMessage("Car details fetched successfully");
            logger.info("### service - Get Car Details - Car details fetched successfully for car ID: {} ###", id);
            return response;
        } catch (Exception e) {
            logger.error("### service - Get Car Details - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CarListResponse getCarsByAgency(Long agencyId) {
        CarListResponse response = new CarListResponse();
        try {
            logger.info("### service - Get Cars By Agency - Begin ###");
            List<Car> cars = carRepository.findByAgencyId(agencyId);
            List<CarListEnvelop> carListEnvelops = cars.stream()
                    .map(this::convertToCarListEnvelop)
                    .collect(Collectors.toList());

            response.setCarsByAgency(carListEnvelops);
            response.setStatus("200");
            response.setMessage("Cars fetched successfully");
            logger.info("### service - Get Cars By Agency - Success ###");
            return response;
        } catch (Exception e) {
            logger.error("### service - Get Cars By Agency - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
    }

    @Override
    public CarListResponse getCarsByLocation(User user) {
        CarListResponse response = new CarListResponse();
        try {
            logger.info("### service - Get Cars By Location - Begin ###");

            // Fetch cars near the user
            List<Car> cars = carRepository.findAll().stream()
                    .filter(car -> car.getStatus().equals(CarStatus.AVAILABLE) &&
                            calculateDistance(user.getLatitude(), user.getLongitude(), car.getAgency().getLatitude(), car.getAgency().getLongitude()) <= 10)
                    .sorted(Comparator.comparing(Car::getPromotion).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            List<CarListEnvelop> carListEnvelops = cars.stream()
                    .map(this::convertToCarListEnvelop)
                    .collect(Collectors.toList());

            // Fetch car recommendations based on clicks
            List<Car> recommendedCars = carRepository.findAll().stream()
                    .filter(car -> car.getStatus().equals(CarStatus.AVAILABLE))
                    .sorted(Comparator.comparing(Car::getClicks).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            List<CarListEnvelop> recommendedCarListEnvelops = recommendedCars.stream()
                    .map(this::convertToCarListEnvelop)
                    .collect(Collectors.toList());

            response.setCarsByLocation(carListEnvelops);
            response.setCarsByClick(recommendedCarListEnvelops);
            response.setStatus("200");
            response.setMessage("Cars fetched successfully");
            logger.info("### service - Get Cars By Location - Success ###");
            return response;
        } catch (Exception e) {
            logger.error("### service - Get Cars By Location - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
            return response;
        }
    }

    private CarListEnvelop convertToCarListEnvelop(Car car) {
        List<String> imagePaths = car.getImages().stream()
                .map(CarImage::getImagePath)
                .collect(Collectors.toList());

        CarListEnvelop carEnvelop = new CarListEnvelop();
        carEnvelop.setId(car.getId().toString());
        carEnvelop.setMake(car.getMake());
        carEnvelop.setModel(car.getModel());
        carEnvelop.setType(car.getType());
        carEnvelop.setPrice(car.getPrice());
        carEnvelop.setImageFileNames(imagePaths);
        carEnvelop.setPromotion(car.getPromotion());
        carEnvelop.setPercentage(car.getPercentage());
        return carEnvelop;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Convert to kilometers

        return distance;
    }

    @Override
    public CarListResponse searchCars(SearchCriteria criteria) {
        CarListResponse response = new CarListResponse();
        try {
            logger.info("### service - Search Cars - Begin ###");
            logger.info("Search criteria: {}", criteria);

            Specification<Car> specification = CarSpecification.search(criteria);
            List<Car> cars = carRepository.findAll(specification);

            List<CarListEnvelop> carsWithPromotions = cars.stream()
                    .filter(Car::getPromotion)
                    .map(this::convertToCarListEnvelop)
                    .collect(Collectors.toList());

            List<CarListEnvelop> otherCars = cars.stream()
                    .filter(car -> !car.getPromotion())
                    .map(this::convertToCarListEnvelop)
                    .collect(Collectors.toList());

            logger.info("Number of cars with promotions found: {}", carsWithPromotions.size());
            logger.info("Number of other cars found: {}", otherCars.size());
            logger.info("### service - Search Cars - End ###");

            response.setCarsWithPromotions(carsWithPromotions);
            response.setOtherCars(otherCars);
            response.setStatus("200");
            response.setMessage("Cars fetched successfully");
        } catch (Exception e) {
            logger.error("### service - Search Cars - Technical error - End ###", e);
            response.setStatus("500");
            response.setMessage("Technical error: " + e.getMessage());
        }
        return response;
    }


}
