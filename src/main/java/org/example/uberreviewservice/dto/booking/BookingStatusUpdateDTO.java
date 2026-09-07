package org.example.uberreviewservice.dto.booking;

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
    private BookingStatus newStatus;
}
