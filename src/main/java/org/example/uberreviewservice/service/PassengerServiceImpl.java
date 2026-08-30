package org.example.uberreviewservice.service;

import com.rideflow.rideflowentityservice.models.Passenger;
import lombok.AllArgsConstructor;
import org.example.uberreviewservice.dto.passenger.*;
import org.example.uberreviewservice.exception.PassengerHasActiveBookingsException;
import org.example.uberreviewservice.exception.PassengerNotFoundException;
import org.example.uberreviewservice.mapper.PassengerMapper;
import org.example.uberreviewservice.repository.BookingRepository;
import org.example.uberreviewservice.repository.PassengerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    @Override
    public PassengerResponseDTO createPassenger(PassengerRequestDTO requestDTO) {
        validatePassengerRequest(requestDTO);

        Passenger passenger = PassengerMapper.toEntity(requestDTO);
        Passenger saved = passengerRepository.save(passenger);
        return PassengerMapper.toResponseDTO(saved);
    }

    @Override
    public PassengerResponseDTO getPassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));
        return PassengerMapper.toResponseDTO(passenger);
    }

    @Override
    public List<PassengerResponseDTO> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .map(PassengerMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PassengerResponseDTO updatePassenger(Long id, PassengerRequestDTO requestDTO) {
        validatePassengerRequest(requestDTO);

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));

        passenger.setPassengerName(requestDTO.getPassengerName());

        Passenger updated = passengerRepository.save(passenger);
        return PassengerMapper.toResponseDTO(updated);
    }

    @Override
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));


        long bookingCount = bookingRepository.countByPassengerId(id);
        if (bookingCount > 0) {
            throw new PassengerHasActiveBookingsException(id, bookingCount);
        }

        passengerRepository.delete(passenger);
    }

    private void validatePassengerRequest(PassengerRequestDTO requestDTO) {
        if (requestDTO.getPassengerName() == null || requestDTO.getPassengerName().isBlank()) {
            throw new IllegalArgumentException("Passenger name must not be empty");
        }
    }
}