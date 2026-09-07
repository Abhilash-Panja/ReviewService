package org.example.uberreviewservice.dto.driver;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

// Used when registering/creating a driver
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequestDTO {
    @Schema(description = "Service rejects null or blank; currently mapped to 500.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Ravi Kumar")
    private String driverName;
    @Schema(description = "Keep this exact API spelling. Service rejects null/blank (500) and checks duplicates (409). Latest shared entity instead spells its property licenseNumber; see compatibility issue.", requiredMode = Schema.RequiredMode.REQUIRED, example = "TS0920260012345")
    private String licenceNumber;
}