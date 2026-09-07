package org.example.uberreviewservice.service;

import org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO;
import org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO;

public interface ReviewService {
    PassengerReviewResponseDTO createReview(PassengerReviewRequestDTO requestDTO);
    PassengerReviewResponseDTO getReview(Long id);
    PassengerReviewResponseDTO getReviewByBookingId(Long bookingId);
}