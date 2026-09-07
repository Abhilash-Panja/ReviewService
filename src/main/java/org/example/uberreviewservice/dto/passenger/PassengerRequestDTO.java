package org.example.uberreviewservice.dto.passenger;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

// Used when registering a new passenger
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequestDTO {
    @Schema(description = "Service rejects null or blank with IllegalArgumentException; the generic advice maps this to 500, not 400.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Anita Sharma")
    private String passengerName;
}