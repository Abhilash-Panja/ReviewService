package org.example.uberreviewservice.dto.passenger;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponseDTO {
    private Long id;
    private String passengerName;
}
