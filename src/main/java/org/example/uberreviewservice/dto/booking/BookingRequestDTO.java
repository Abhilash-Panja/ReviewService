package org.example.uberreviewservice.dto.booking;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    private Long passengerId; // only field client sends; driver, status, timings are system-derived
}