package org.example.uberreviewservice.service;

import org.example.uberreviewservice.dto.driver.DriverRequestDTO;
import org.example.uberreviewservice.dto.driver.DriverResponseDTO;

import java.util.List;

public interface DriverService {
    DriverResponseDTO createDriver(DriverRequestDTO requestDTO);
    DriverResponseDTO getDriver(Long id);
    List<DriverResponseDTO> getAllDrivers();
    DriverResponseDTO updateDriver(Long id, DriverRequestDTO requestDTO);
    void deleteDriver(Long id);
}