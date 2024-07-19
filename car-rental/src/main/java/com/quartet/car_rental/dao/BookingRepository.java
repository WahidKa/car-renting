package com.quartet.car_rental.dao;

import com.quartet.car_rental.dao.entities.Booking;
import com.quartet.car_rental.dao.entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByCar_AgencyId(Long id);

    List<Booking> findByUserIdAndStatus(Long id, BookingStatus bookingStatus);

    List<Booking> findByAgencyIdAndStatus(Long id, BookingStatus bookingStatus);
}
