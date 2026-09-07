package org.example.uberreviewservice.service;

import org.example.uberreviewservice.dto.passenger.PassengerRequestDTO;
import org.example.uberreviewservice.dto.passenger.PassengerResponseDTO;

import java.util.List;

public interface PassengerService {
    PassengerResponseDTO createPassenger(PassengerRequestDTO requestDTO);
    PassengerResponseDTO getPassenger(Long id);
    List<PassengerResponseDTO> getAllPassengers();
    PassengerResponseDTO updatePassenger(Long id, PassengerRequestDTO requestDTO);
    void deletePassenger(Long id);
}