package org.example.uberreviewservice.mapper;
import com.rideflow.rideflowentityservice.models.Booking;
import com.rideflow.rideflowentityservice.models.PassengerReview;
import com.rideflow.rideflowentityservice.models.Review;
import org.example.uberreviewservice.dto.review.PassengerReviewRequestDTO;
import org.example.uberreviewservice.dto.review.PassengerReviewResponseDTO;
import org.example.uberreviewservice.dto.review.ReviewSummaryDTO;

public class ReviewMapper {

    public static ReviewSummaryDTO toSummaryDTO(Review review) {
        if (review == null) return null;

        if (review instanceof PassengerReview passengerReview) {
            return ReviewSummaryDTO.builder()
                    .id(passengerReview.getId())
                    .rating(passengerReview.getPassengerRating())
                    .description(passengerReview.getPassengerReviewContent())
                    .build();
        }

        return ReviewSummaryDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .description(review.getDescription())
                .build();
    }

    public static PassengerReviewResponseDTO toResponseDTO(PassengerReview review) {
        if (review == null) return null;
        Booking booking = review.getBooking();
        return PassengerReviewResponseDTO.builder()
                .id(review.getId())
                .passengerRating(review.getPassengerRating())
                .passengerReviewContent(review.getPassengerReviewContent())
                .bookingId(booking.getId())
                .passengerId(booking.getPassenger().getId())
                .passengerName(booking.getPassenger().getPassengerName())
                .build();
    }

    // Needs the resolved Booking passed in — same reasoning as BookingMapper not
    // having a static toEntity: this isn't pure field mapping, it depends on a
    // Booking already fetched from the DB by the service layer
    public static PassengerReview toEntity(PassengerReviewRequestDTO dto, Booking booking) {
        return PassengerReview.builder()
                .passengerReviewContent(dto.getPassengerReviewContent())
                .passengerRating(dto.getPassengerRating())
                .booking(booking)
                .build();
    }
}