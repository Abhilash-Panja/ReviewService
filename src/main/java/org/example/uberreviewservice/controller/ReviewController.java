package org.example.uberreviewservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO;
import org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO;
import org.example.uberreviewservice.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Passenger reviews")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService; // depends on the interface, not ReviewServiceImpl

    @Operation(operationId = "ReviewService_createReview", summary = "Create a passenger review",
            description = "Creates the one permitted review for a completed ride and demonstrates JOINED inheritance persistence. ReviewController.createReview -> ReviewServiceImpl.createReview -> BookingRepository.findById -> COMPLETED check -> ReviewRepository.findByBookingId duplicate check -> validateReviewRequest -> ReviewMapper.toEntity -> PassengerReviewRepository.save -> ReviewMapper.toResponseDTO. Request fields description/rating do not exist in this DTO. Passenger-specific values are persisted; base description/rating are not set by ReviewMapper. Caller identity is not verified.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO.class),
                    examples = @ExampleObject(value = "{\n  \"bookingId\": 301,\n  \"passengerRating\": 4.5,\n  \"passengerReviewContent\": \"Passenger was ready at pickup and communicated clearly.\"\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Create a passenger review completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO.class), examples = @ExampleObject(value = "{\"id\":501,\"passengerRating\":4.5,\"passengerReviewContent\":\"Passenger was ready at pickup and communicated clearly.\",\"bookingId\":301,\"passengerId\":101,\"passengerName\":\"Anita S. Sharma\"}"))),
            @ApiResponse(responseCode = "404", description = "BookingNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "InvalidBookingStateForReviewException or ReviewAlreadyExistsException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Null bookingId, blank content, rating outside 0..5, persistence or catch-all failure.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<PassengerReviewResponseDTO> createReview(
            @RequestBody PassengerReviewRequestDTO requestDTO) {
        PassengerReviewResponseDTO response = reviewService.createReview(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(operationId = "ReviewService_getReview", summary = "Read a passenger review by review ID",
            description = "Confirms the generated review ID can retrieve the saved passenger-specific values. ReviewController.getReview -> ReviewServiceImpl.getReview -> PassengerReviewRepository.findById -> ReviewMapper.toResponseDTO, which follows review.booking.passenger. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Read a passenger review by review ID completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO.class), examples = @ExampleObject(value = "{\"id\":501,\"passengerRating\":4.5,\"passengerReviewContent\":\"Passenger was ready at pickup and communicated clearly.\",\"bookingId\":301,\"passengerId\":101,\"passengerName\":\"Anita S. Sharma\"}"))),
            @ApiResponse(responseCode = "404", description = "ReviewNotFoundException.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Repository/mapping or catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PassengerReviewResponseDTO> getReview(@Parameter(description = "Database ID of the resource selected by this path; not a list index.", required = true, example = "1") @PathVariable Long id) {
        PassengerReviewResponseDTO response = reviewService.getReview(id);
        return ResponseEntity.ok(response);
    }

    @Operation(operationId = "ReviewService_getReviewByBooking", summary = "Read a passenger review by booking ID",
            description = "Retrieves the same review when the client knows the booking ID rather than the review ID. ReviewController.getReviewByBooking -> ReviewServiceImpl.getReviewByBookingId -> PassengerReviewRepository.findByBookingId -> ReviewMapper.toResponseDTO. The exception message says Review not found with id even though this route passes a booking ID to that exception constructor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Read a passenger review by booking ID completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO.class), examples = @ExampleObject(value = "{\"id\":501,\"passengerRating\":4.5,\"passengerReviewContent\":\"Passenger was ready at pickup and communicated clearly.\",\"bookingId\":301,\"passengerId\":101,\"passengerName\":\"Anita S. Sharma\"}"))),
            @ApiResponse(responseCode = "404", description = "ReviewNotFoundException if no passenger review exists for that booking ID; no separate booking existence lookup.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Repository/mapping or catch-all error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.example.uberreviewservice.dto.error.ErrorResponseDTO.class)))
    })
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PassengerReviewResponseDTO> getReviewByBooking(
            @Parameter(description = "Database booking ID, not a review ID.", required = true, example = "1") @PathVariable Long bookingId) {
        PassengerReviewResponseDTO response = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
}