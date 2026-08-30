package org.example.uberreviewservice.mapper;

import com.rideflow.rideflowentityservice.models.Passenger;
import org.example.uberreviewservice.dto.passenger.*;


public class PassengerMapper {

    public static PassengerSummaryDTO toSummaryDTO(Passenger passenger) {
        if (passenger == null) return null;
        return PassengerSummaryDTO.builder()
                .id(passenger.getId())
                .passengerName(passenger.getPassengerName())
                .build();
    }

    public static PassengerResponseDTO toResponseDTO(Passenger passenger) {
        if (passenger == null) return null;
        return PassengerResponseDTO.builder()
                .id(passenger.getId())
                .passengerName(passenger.getPassengerName())
                .build();
    }

    // Simple field mapping, no cross-entity lookups needed — safe to keep static here,
    // same reasoning as DriverMapper.toEntity()
    public static Passenger toEntity(PassengerRequestDTO dto) {
        if (dto == null) return null;
        return Passenger.builder()
                .passengerName(dto.getPassengerName())
                .build();
    }
}