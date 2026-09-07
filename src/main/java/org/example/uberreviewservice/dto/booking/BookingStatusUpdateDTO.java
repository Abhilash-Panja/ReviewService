package org.example.uberreviewservice.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import com.rideflow.rideflowentityservice.models.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatusUpdateDTO {
    @NotNull(message = "newStatus is required")
    @Schema(description = "The only ReviewService request field with active @Valid plus @NotNull; missing/null yields 400. Transition rules are checked separately (409).", requiredMode = Schema.RequiredMode.REQUIRED, example = "CAB_ARRIVED")
    private BookingStatus newStatus;
}
