package org.example.uberreviewservice.dto.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequestDTO {
    @NotBlank(message = "driverName is required")
    @Schema(description = "Driver's display name.", requiredMode = Schema.RequiredMode.REQUIRED, example = "Ravi Kumar")
    private String driverName;

    @NotBlank(message = "licenceNumber is required")
    @Schema(description = "Unique licence number.", requiredMode = Schema.RequiredMode.REQUIRED, example = "TS0920260012345")
    private String licenceNumber;
}
