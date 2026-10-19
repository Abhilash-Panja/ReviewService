package org.example.uberreviewservice.dto.passenger;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequestDTO {
    @NotBlank(message = "passengerName is required")
    @Schema(description = "Passenger's display name.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Anita Sharma")
    private String passengerName;
}
