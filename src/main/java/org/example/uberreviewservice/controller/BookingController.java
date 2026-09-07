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
import org.example.uberreviewservice.dto.booking.*;
import org.example.uberreviewservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Review workflow - bookings")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(operationId = "ReviewService_createBooking", summary = "Create a review-workflow booking",
            description = "Creates a booking with the state needed for the review lifecycle exercise. BookingController.createBooking -> BookingServiceImpl.createBooking -> PassengerRepository.findById -> findAvailableDriver (DriverRepository.findAll().stream().findFirst()) -> BookingRepository.save -> BookingMapper.toResponseDTO(saved, null). This service does not call Redis, Location-Service, Socket-Server or Kafka. It does not check driver availability or proximity. No @Valid on this create request.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"passengerId\": 101\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Create a review-workflow booking completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingResponseDTO.class), examples = @ExampleObject(value = "{\"id\":301,\"startTime\":null,\"endTime\":null,\"totalDistance\":0,\"bookingStatus\":\"ASSIGNED_DRIVER\",\"driver\":{\"id\":201,\"driverName\":\"Ravi K. Kumar\"},\"passenger\":{\"id\":101,\"passengerName\":\"Anita S. Sharma\"},\"review\":null}"))),
            @ApiResponse(responseCode = "404", description = "PassengerNotFoundException for a non-null unknown passenger ID.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "503", description = "NoDriversAvailableException when driverRepository.findAll() is empty.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Missing passengerId, persistence/audit incompatibility, or other catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody BookingRequestDTO requestDTO) {
        BookingResponseDTO response = bookingService.createBooking(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(operationId = "ReviewService_getBooking", summary = "Read a booking and its review summary",
            description = "Reads persisted state after creation, status changes, and review creation. BookingController.getBooking -> BookingServiceImpl.getBooking -> BookingRepository.findById and ReviewRepository.findByBookingId -> ReviewMapper.toSummaryDTO if present -> BookingMapper.toResponseDTO. The embedded review summary may show rating=0.0 and description=null even though the dedicated passenger-review endpoint returns your 4.5/content. This is the current mapping behavior.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Read a booking and its review summary completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingResponseDTO.class), examples = @ExampleObject(value = "{\"id\":301,\"startTime\":null,\"endTime\":null,\"totalDistance\":0,\"bookingStatus\":\"ASSIGNED_DRIVER\",\"driver\":{\"id\":201,\"driverName\":\"Ravi K. Kumar\"},\"passenger\":{\"id\":101,\"passengerName\":\"Anita S. Sharma\"},\"review\":null}"))),
            @ApiResponse(responseCode = "404", description = "BookingNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Repository, mapping or catch-all failure.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @Operation(operationId = "ReviewService_getAllBookings", summary = "List bookings with review summaries",
            description = "Checks the list projection and lets you compare multiple booking states. BookingController.getAllBookings -> BookingServiceImpl.getAllBookings -> BookingRepository.findAll and ReviewRepository.findAll; build a map keyed by booking ID, then BookingMapper.toResponseDTO for each booking. The explicit review fetch avoids one review lookup per booking, but lazy entity relationships can still cause extra SQL; do not interpret the code comment as a guarantee of only two database queries.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List bookings with review summaries completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingResponseDTO.class)), examples = @ExampleObject(value = "[{\"id\":301,\"startTime\":null,\"endTime\":null,\"totalDistance\":0,\"bookingStatus\":\"ASSIGNED_DRIVER\",\"driver\":{\"id\":201,\"driverName\":\"Ravi K. Kumar\"},\"passenger\":{\"id\":101,\"passengerName\":\"Anita S. Sharma\"},\"review\":null}]"))),
            @ApiResponse(responseCode = "500", description = "Repository/mapping/catch-all error; duplicate review keys would also fail the toMap operation.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @Operation(operationId = "ReviewService_updateStatus", summary = "Advance the review booking state",
            description = "Moves the booking through the exact state machine required before a passenger review can be created. BookingController.updateStatus validates DTO -> BookingServiceImpl.updateStatus -> BookingRepository.findById -> ALLOWED_TRANSITIONS check -> update status; set startTime at STARTED and endTime at COMPLETED -> BookingRepository.save -> BookingMapper. COMPLETED also queries ReviewRepository. CANCELED is allowed from ASSIGNED_DRIVER, CAB_ARRIVED or STARTED only. COMPLETED/CANCELED are terminal; IN_RIDE permits only COMPLETED. SCHEDULED and ASSIGNING_DRIVER have no transitions in this service.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingStatusUpdateDTO.class),
                    examples = @ExampleObject(value = "{\n  \"newStatus\": \"CAB_ARRIVED\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Advance the review booking state completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.booking.BookingResponseDTO.class), examples = @ExampleObject(value = "{\"id\":301,\"startTime\":null,\"endTime\":null,\"totalDistance\":0,\"bookingStatus\":\"CAB_ARRIVED\",\"driver\":{\"id\":201,\"driverName\":\"Ravi K. Kumar\"},\"passenger\":{\"id\":101,\"passengerName\":\"Anita S. Sharma\"},\"review\":null}"))),
            @ApiResponse(responseCode = "400", description = "@Valid / @NotNull violation: newStatus: newStatus is required.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "BookingNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "InvalidBookingStatusTransitionException, including same-state, skipped or backward transitions.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Unknown enum, malformed JSON, nonnumeric path, or another error reaching catch-all advice.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(
            @Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateDTO statusUpdateDTO) {
        return ResponseEntity.ok(bookingService.updateStatus(id, statusUpdateDTO));
    }
}