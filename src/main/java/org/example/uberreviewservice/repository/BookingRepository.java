package org.example.uberreviewservice.repository;


import com.rideflow.rideflowentityservice.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    long countByPassengerId(Long passengerId);
    long countByDriverId(Long driverId);
}
