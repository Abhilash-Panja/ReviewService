package org.example.uberreviewservice.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    @Schema(description = "Existing passenger ID. Null reaches findById and becomes 500. Unknown non-null ID becomes 404. Driver is chosen by the service.", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    private Long passengerId; // only field client sends; driver, status, timings are system-derived
}