package org.example.uberreviewservice.repository;


import com.rideflow.rideflowentityservice.models.PassengerReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerReviewRepository extends JpaRepository<PassengerReview, Long> {
    Optional<PassengerReview> findByBookingId(Long bookingId);
}
