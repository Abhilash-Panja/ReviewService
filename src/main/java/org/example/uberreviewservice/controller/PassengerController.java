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

import lombok.AllArgsConstructor;
import org.example.uberreviewservice.dto.passenger.*;
import org.example.uberreviewservice.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review workflow - passengers")
@RestController
@RequestMapping("/api/v1/passengers")
@AllArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @Operation(operationId = "ReviewService_createPassenger", summary = "Create a passenger for the review workflow",
            description = "Creates a passenger record used by ReviewService bookings. This is independent of AuthService login. PassengerController.createPassenger -> PassengerServiceImpl.createPassenger -> validatePassengerRequest -> PassengerMapper.toEntity -> PassengerRepository.save -> PassengerMapper.toResponseDTO. The checked-in source depends on historical 0.0.2-SNAPSHOT. With current shared models this DTO omits mandatory account/role fields; simply upgrading the dependency will not preserve this workflow.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"passengerName\": \"Anita Sharma\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Create a passenger for the review workflow completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerResponseDTO.class), examples = @ExampleObject(value = "{\"id\":101,\"passengerName\":\"Anita Sharma\"}"))),
            @ApiResponse(responseCode = "500", description = "Blank/missing service-validated field, persistence error, or catch-all failure; ErrorResponseDTO.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(
            @RequestBody PassengerRequestDTO requestDTO) {
        PassengerResponseDTO response = passengerService.createPassenger(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(operationId = "ReviewService_getPassenger", summary = "Read one passenger",
            description = "Confirms the created passenger can be found by the returned database ID. PassengerController.getPassenger -> PassengerServiceImpl.getPassenger -> PassengerRepository.findById -> PassengerMapper.toResponseDTO; missing row throws PassengerNotFoundException. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Read one passenger completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerResponseDTO.class), examples = @ExampleObject(value = "{\"id\":101,\"passengerName\":\"Anita Sharma\"}"))),
            @ApiResponse(responseCode = "404", description = "PassengerNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Catch-all error, including nonnumeric path binding in this advice.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassenger(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getPassenger(id));
    }

    @Operation(operationId = "ReviewService_getAllPassengers", summary = "List all passengers",
            description = "Checks the collection view and confirms the created passenger is included. PassengerController.getAllPassengers -> PassengerServiceImpl.getAllPassengers -> PassengerRepository.findAll -> PassengerMapper.toResponseDTO for every row. No pagination, filtering, sorting, or query parameters are declared.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List all passengers completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerResponseDTO.class)), examples = @ExampleObject(value = "[{\"id\":101,\"passengerName\":\"Anita Sharma\"}]"))),
            @ApiResponse(responseCode = "500", description = "Repository, mapping or other catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAllPassengers() {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @Operation(operationId = "ReviewService_updatePassenger", summary = "Update a passenger",
            description = "Exercises the update path while keeping identifiers stable. PassengerController.updatePassenger -> PassengerServiceImpl.updatePassenger: validate request, PassengerRepository.findById, set passengerName, PassengerRepository.save, PassengerMapper.toResponseDTO. Send every field of this request DTO; omitted fields are validated as null. This is not a partial update.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"passengerName\": \"Anita S. Sharma\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update a passenger completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.passenger.PassengerResponseDTO.class), examples = @ExampleObject(value = "{\"id\":101,\"passengerName\":\"Anita S. Sharma\"}"))),
            @ApiResponse(responseCode = "404", description = "PassengerNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Blank/missing field or other catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(
            @Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id,
            @RequestBody PassengerRequestDTO requestDTO) {
        return ResponseEntity.ok(passengerService.updatePassenger(id, requestDTO));
    }

    @Operation(operationId = "ReviewService_deletePassenger", summary = "Delete an unreferenced passenger",
            description = "Tests deletion and the foreign-key protection rule using a separate unbooked fixture. PassengerController.deletePassenger -> PassengerServiceImpl.deletePassenger -> PassengerRepository.findById -> BookingRepository.countByPassengerId; if count is zero, PassengerRepository.delete. Controller returns noContent(). The count includes completed and canceled bookings as well as active ones. No booking-delete API exists, so completion does not make the original fixture deletable.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No response body.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "PassengerNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "PassengerHasActiveBookingsException when any booking references this row.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Other persistence or catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}