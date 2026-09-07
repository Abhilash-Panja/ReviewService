package org.example.uberreviewservice.dto.driver;

import lombok.*;

// Used when registering/creating a driver
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequestDTO {
    private String driverName;
    private String licenceNumber;
}