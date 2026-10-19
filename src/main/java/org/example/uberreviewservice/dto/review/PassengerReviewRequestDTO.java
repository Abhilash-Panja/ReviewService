package org.example.uberreviewservice.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerReviewRequestDTO {
    @NotNull(message = "bookingId is required")
    @Schema(description = "Existing COMPLETED booking without a review.", requiredMode = Schema.RequiredMode.REQUIRED, example = "301")
    private Long bookingId;

    @NotNull(message = "passengerRating is required")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    @Schema(description = "Rating from 0 through 5.", requiredMode = Schema.RequiredMode.REQUIRED, example = "4.5", minimum = "0", maximum = "5")
    private Double passengerRating;

    @NotBlank(message = "passengerReviewContent is required")
    @Schema(description = "Review of the passenger.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Passenger was ready at pickup.")
    private String passengerReviewContent;
}
