package org.example.uberreviewservice.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    @NotNull(message = "passengerId is required")
    @Schema(description = "Existing passenger ID. Driver is chosen by the service.", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    private Long passengerId;
}
