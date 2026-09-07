package org.example.uberreviewservice.dto.driver;

import lombok.*;

// Lightweight version, embedded inside BookingResponseDTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverSummaryDTO {
    private Long id;
    private String driverName;
}