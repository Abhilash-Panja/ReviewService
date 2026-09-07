package org.example.uberreviewservice.dto.review;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerReviewResponseDTO {
    private Long id;
    private double passengerRating;
    private String passengerReviewContent;
    private Long bookingId;
    private Long passengerId;
    private String passengerName;   // small denormalized convenience field for UI
}