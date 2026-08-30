package org.example.uberreviewservice.service;

import com.rideflow.rideflowentityservice.models.Driver;
import lombok.AllArgsConstructor;
import org.example.uberreviewservice.dto.driver.DriverRequestDTO;
import org.example.uberreviewservice.dto.driver.DriverResponseDTO;
import org.example.uberreviewservice.exception.DriverHasActiveBookingsException;
import org.example.uberreviewservice.exception.DriverNotFoundException;
import org.example.uberreviewservice.exception.DuplicateLicenceNumberException;
import org.example.uberreviewservice.mapper.DriverMapper;
import org.example.uberreviewservice.repository.BookingRepository;
import org.example.uberreviewservice.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;

    @Override
    public DriverResponseDTO createDriver(DriverRequestDTO requestDTO) {
        validateDriverRequest(requestDTO);

        if (driverRepository.existsByLicenceNumber(requestDTO.getLicenceNumber())) {
            throw new DuplicateLicenceNumberException(requestDTO.getLicenceNumber());
        }

        Driver driver = DriverMapper.toEntity(requestDTO);
        Driver saved = driverRepository.save(driver);
        return DriverMapper.toResponseDTO(saved);
    }

    @Override
    public DriverResponseDTO getDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
        return DriverMapper.toResponseDTO(driver);
    }

    @Override
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverMapper::toResponseDTO)
                .toList();
    }

    @Override
    public DriverResponseDTO updateDriver(Long id, DriverRequestDTO requestDTO) {
        validateDriverRequest(requestDTO);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

        // Only check for duplicates if the licence number is actually changing —
        // otherwise updating just the name would falsely trip the check against
        // the driver's own existing licence number
        if (!driver.getLicenceNumber().equals(requestDTO.getLicenceNumber())
                && driverRepository.existsByLicenceNumber(requestDTO.getLicenceNumber())) {
            throw new DuplicateLicenceNumberException(requestDTO.getLicenceNumber());
        }

        driver.setDriverName(requestDTO.getDriverName());
        driver.setLicenceNumber(requestDTO.getLicenceNumber());

        Driver updated = driverRepository.save(driver);
        return DriverMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

        long bookingCount = bookingRepository.countByDriverId(id);
        if (bookingCount > 0) {
            throw new DriverHasActiveBookingsException(id, bookingCount);
        }

        driverRepository.delete(driver);
    }

    private void validateDriverRequest(DriverRequestDTO requestDTO) {
        if (requestDTO.getDriverName() == null || requestDTO.getDriverName().isBlank()) {
            throw new IllegalArgumentException("Driver name must not be empty");
        }
        if (requestDTO.getLicenceNumber() == null || requestDTO.getLicenceNumber().isBlank()) {
            throw new IllegalArgumentException("Licence number must not be empty");
        }
    }
}
