package org.example.uberreviewservice.mapper;

import com.rideflow.rideflowentityservice.models.Driver;
import org.example.uberreviewservice.dto.driver.*;


public class DriverMapper {

    public static DriverSummaryDTO toSummaryDTO(Driver driver) {
        if (driver == null) return null;
        return DriverSummaryDTO.builder()
                .id(driver.getId())
                .driverName(driver.getDriverName())
                .build();
    }

    public static DriverResponseDTO toResponseDTO(Driver driver) {
        if (driver == null) return null;
        return DriverResponseDTO.builder()
                .id(driver.getId())
                .driverName(driver.getDriverName())
                .licenceNumber(driver.getLicenceNumber())
                .build();
    }

    // Safe to keep here: unlike Booking, creating a Driver needs no cross-entity
    // lookups or business logic — it's a pure 1:1 field mapping, so a static
    // toEntity is fine and doesn't blur mapper/service responsibility.
    public static Driver toEntity(DriverRequestDTO dto) {
        if (dto == null) return null;
        return Driver.builder()
                .driverName(dto.getDriverName())
                .licenceNumber(dto.getLicenceNumber())
                .build();
    }
}