package org.example.uberreviewservice.dto.review;

import lombok.*;

// Sent by driver after ride completion, to rate the passenger
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerReviewRequestDTO {
    private Long bookingId;              // which ride this review is for
    private double passengerRating;
    private String passengerReviewContent;
}