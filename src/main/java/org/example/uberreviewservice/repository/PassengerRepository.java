package org.example.uberreviewservice.repository;


import com.rideflow.rideflowentityservice.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
}
