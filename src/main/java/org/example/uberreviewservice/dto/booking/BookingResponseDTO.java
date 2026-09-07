package org.example.uberreviewservice.dto.booking;

import com.rideflow.rideflowentityservice.models.BookingStatus;
import lombok.*;
import org.example.uberreviewservice.dto.driver.DriverSummaryDTO;
import org.example.uberreviewservice.dto.passenger.PassengerSummaryDTO;
import org.example.uberreviewservice.dto.review.ReviewSummaryDTO;


import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private long totalDistance;
    private BookingStatus bookingStatus;

    private DriverSummaryDTO driver;
    private PassengerSummaryDTO passenger;
    private ReviewSummaryDTO review; // null until the ride is reviewed
}
