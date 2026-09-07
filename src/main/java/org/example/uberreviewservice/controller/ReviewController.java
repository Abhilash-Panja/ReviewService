package org.example.uberreviewservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO;
import org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO;
import org.example.uberreviewservice.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService; // depends on the interface, not ReviewServiceImpl

    @PostMapping
    public ResponseEntity<PassengerReviewResponseDTO> createReview(
            @RequestBody PassengerReviewRequestDTO requestDTO) {
        PassengerReviewResponseDTO response = reviewService.createReview(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerReviewResponseDTO> getReview(@PathVariable Long id) {
        PassengerReviewResponseDTO response = reviewService.getReview(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PassengerReviewResponseDTO> getReviewByBooking(
            @PathVariable Long bookingId) {
        PassengerReviewResponseDTO response = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
}