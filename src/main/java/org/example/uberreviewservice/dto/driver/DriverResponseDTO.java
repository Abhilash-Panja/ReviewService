package org.example.uberreviewservice.dto.driver;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponseDTO {
    private Long id;
    private String driverName;
    private String licenceNumber;
}