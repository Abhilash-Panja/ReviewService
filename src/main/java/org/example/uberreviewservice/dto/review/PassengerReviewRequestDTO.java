package org.example.uberreviewservice.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

// Sent by driver after ride completion, to rate the passenger
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerReviewRequestDTO {
    @Schema(description = "Existing COMPLETED booking without a review. These checks precede content and rating validation.", requiredMode = Schema.RequiredMode.REQUIRED, example = "301")
    private Long bookingId;              // which ride this review is for
    @Schema(description = "Range 0 through 5 inclusive checked in service; out-of-range becomes 500. Primitive double defaults to 0 when omitted.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "4.5", minimum = "0", maximum = "5", defaultValue = "0")
    private double passengerRating;
    @Schema(description = "Review of the passenger, written by a driver conceptually; caller identity is not enforced. Null/blank becomes 500 after booking checks.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Passenger was ready at pickup and communicated clearly.")
    private String passengerReviewContent;
}