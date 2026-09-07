package org.example.uberreviewservice.repository;


import com.rideflow.rideflowentityservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
    boolean existsByLicenseNumber(String licenseNumber);
}
