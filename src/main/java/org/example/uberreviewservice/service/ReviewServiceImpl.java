package org.example.uberreviewservice.service;

import com.rideflow.rideflowentityservice.models.Booking;
import com.rideflow.rideflowentityservice.models.BookingStatus;
import com.rideflow.rideflowentityservice.models.PassengerReview;
import lombok.RequiredArgsConstructor;
import org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO;
import org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO;
import org.example.uberreviewservice.exception.BookingNotFoundException;
import org.example.uberreviewservice.exception.InvalidBookingStateForReviewException;
import org.example.uberreviewservice.exception.ReviewAlreadyExistsException;
import org.example.uberreviewservice.exception.ReviewNotFoundException;
import org.example.uberreviewservice.mapper.ReviewMapper;
import org.example.uberreviewservice.repository.BookingRepository;
import org.example.uberreviewservice.repository.PassengerReviewRepository;
import org.example.uberreviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final PassengerReviewRepository passengerReviewRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Override
    public PassengerReviewResponseDTO createReview(PassengerReviewRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(requestDTO.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(requestDTO.getBookingId()));

        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new InvalidBookingStateForReviewException(booking.getBookingStatus().name());
        }

        if (reviewRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new ReviewAlreadyExistsException(booking.getId());
        }

        validateReviewRequest(requestDTO);

        // This line is exactly where the earlier bug lived — PassengerReview.builder()
        // now correctly resolves to a builder chaining through Review -> BaseModel,
        // thanks to @SuperBuilder being applied across the whole hierarchy
        PassengerReview review = ReviewMapper.toEntity(requestDTO, booking);
        PassengerReview saved = passengerReviewRepository.save(review);
        return ReviewMapper.toResponseDTO(saved);
    }

    @Override
    public PassengerReviewResponseDTO getReview(Long id) {
        PassengerReview review = passengerReviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return ReviewMapper.toResponseDTO(review);
    }

    @Override
    public PassengerReviewResponseDTO getReviewByBookingId(Long bookingId) {
        PassengerReview review = passengerReviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ReviewNotFoundException(bookingId));
        return ReviewMapper.toResponseDTO(review);
    }

    private void validateReviewRequest(PassengerReviewRequestDTO requestDTO) {
        if (requestDTO.getPassengerReviewContent() == null
                || requestDTO.getPassengerReviewContent().isBlank()) {
            throw new IllegalArgumentException("Review content must not be empty");
        }
        if (requestDTO.getPassengerRating() < 0 || requestDTO.getPassengerRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
}