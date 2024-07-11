package com.quartet.car_rental.dao.specification;

import com.quartet.car_rental.dao.entities.Car;
import com.quartet.car_rental.dao.entities.CarStatus;
import com.quartet.car_rental.dto.request.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class CarSpecification {

    public static Specification<Car> search(SearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (criteria.getMake() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("make"), criteria.getMake()));
            }
            if (criteria.getModel() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("model"), criteria.getModel()));
            }
            if (criteria.getType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), criteria.getType()));
            }
            if (criteria.getTransmissionType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("transmissionType"), criteria.getTransmissionType()));
            }
            if (criteria.getFuelType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fuelType"), criteria.getFuelType()));
            }
            if (criteria.getMinPrice() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }
            predicate = cb.and(predicate, cb.equal(root.get("status"), CarStatus.AVAILABLE));
            return predicate;
        };
    }
}

