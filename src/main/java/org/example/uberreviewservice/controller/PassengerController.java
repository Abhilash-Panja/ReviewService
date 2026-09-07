package org.example.uberreviewservice.controller;

import lombok.AllArgsConstructor;
import org.example.uberreviewservice.dto.passenger.*;
import org.example.uberreviewservice.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/passengers")
@AllArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(
            @RequestBody PassengerRequestDTO requestDTO) {
        PassengerResponseDTO response = passengerService.createPassenger(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassenger(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getPassenger(id));
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAllPassengers() {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(
            @PathVariable Long id,
            @RequestBody PassengerRequestDTO requestDTO) {
        return ResponseEntity.ok(passengerService.updatePassenger(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}