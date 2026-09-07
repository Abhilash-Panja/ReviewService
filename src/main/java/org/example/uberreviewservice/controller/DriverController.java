package org.example.uberreviewservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Review workflow - drivers")
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService; // depends on the interface, not DriverServiceImpl

    @Operation(operationId = "ReviewService_createDriver", summary = "Create a driver for the review workflow",
            description = "Creates a driver record used by ReviewService bookings. This is independent of AuthService login. DriverController.createDriver -> DriverServiceImpl.createDriver -> validateDriverRequest -> DriverMapper.toEntity -> DriverRepository.save -> DriverMapper.toResponseDTO. The service checks existsByLicenceNumber and returns 409 on a duplicate. The checked-in source depends on historical 0.0.2-SNAPSHOT. With current shared models this DTO omits mandatory account/role fields; simply upgrading the dependency will not preserve this workflow.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"driverName\": \"Ravi Kumar\",\n  \"licenceNumber\": \"TS0920260012345\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Create a driver for the review workflow completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverResponseDTO.class), examples = @ExampleObject(value = "{\"id\":201,\"driverName\":\"Ravi Kumar\",\"licenceNumber\":\"TS0920260012345\"}"))),
            @ApiResponse(responseCode = "409", description = "DuplicateLicenceNumberException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Blank/missing service-validated field, persistence error, or catch-all failure; ErrorResponseDTO.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(
            @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO response = driverService.createDriver(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(operationId = "ReviewService_getDriver", summary = "Read one driver",
            description = "Confirms the created driver can be found by the returned database ID. DriverController.getDriver -> DriverServiceImpl.getDriver -> DriverRepository.findById -> DriverMapper.toResponseDTO; missing row throws DriverNotFoundException. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Read one driver completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverResponseDTO.class), examples = @ExampleObject(value = "{\"id\":201,\"driverName\":\"Ravi Kumar\",\"licenceNumber\":\"TS0920260012345\"}"))),
            @ApiResponse(responseCode = "404", description = "DriverNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Catch-all error, including nonnumeric path binding in this advice.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriver(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        DriverResponseDTO response = driverService.getDriver(id);
        return ResponseEntity.ok(response);
    }

    @Operation(operationId = "ReviewService_getAllDrivers", summary = "List all drivers",
            description = "Checks the collection view and confirms the created driver is included. DriverController.getAllDrivers -> DriverServiceImpl.getAllDrivers -> DriverRepository.findAll -> DriverMapper.toResponseDTO for every row. No pagination, filtering, sorting, or query parameters are declared.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List all drivers completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverResponseDTO.class)), examples = @ExampleObject(value = "[{\"id\":201,\"driverName\":\"Ravi Kumar\",\"licenceNumber\":\"TS0920260012345\"}]"))),
            @ApiResponse(responseCode = "500", description = "Repository, mapping or other catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<DriverResponseDTO> response = driverService.getAllDrivers();
        return ResponseEntity.ok(response);
    }

    @Operation(operationId = "ReviewService_updateDriver", summary = "Update a driver",
            description = "Exercises the update path while keeping identifiers stable. DriverController.updateDriver -> DriverServiceImpl.updateDriver: validate request, DriverRepository.findById, set driverName and licenceNumber, DriverRepository.save, DriverMapper.toResponseDTO. The service checks existsByLicenceNumber and returns 409 on a duplicate. Send every field of this request DTO; omitted fields are validated as null. This is not a partial update.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"driverName\": \"Ravi K. Kumar\",\n  \"licenceNumber\": \"TS0920260012345\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update a driver completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.driver.DriverResponseDTO.class), examples = @ExampleObject(value = "{\"id\":201,\"driverName\":\"Ravi K. Kumar\",\"licenceNumber\":\"TS0920260012345\"}"))),
            @ApiResponse(responseCode = "404", description = "DriverNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Changed licence number already belongs to another driver.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Blank/missing field or other catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id,
            @RequestBody DriverRequestDTO requestDTO) {
        DriverResponseDTO response = driverService.updateDriver(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(operationId = "ReviewService_deleteDriver", summary = "Delete an unreferenced driver",
            description = "Tests deletion and the foreign-key protection rule using a separate unbooked fixture. DriverController.deleteDriver -> DriverServiceImpl.deleteDriver -> DriverRepository.findById -> BookingRepository.countByDriverId; if count is zero, DriverRepository.delete. Controller returns noContent(). The count includes completed and canceled bookings as well as active ones. No booking-delete API exists, so completion does not make the original fixture deletable.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No response body.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "DriverNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "DriverHasActiveBookingsException when any booking references this row.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Other persistence or catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}