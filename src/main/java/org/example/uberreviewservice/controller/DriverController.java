package org.example.uberreviewservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.uberreviewservice.dto.driver.DriverRequestDTO;
import org.example.uberreviewservice.dto.driver.DriverResponseDTO;
import org.example.uberreviewservice.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService; // depends on the interface, not DriverServiceImpl

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(
            @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO response = driverService.createDriver(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriver(@PathVariable Long id) {
        DriverResponseDTO response = driverService.getDriver(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> response = driverService.getAllDrivers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable Long id,
            @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO response = driverService.updateDriver(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}