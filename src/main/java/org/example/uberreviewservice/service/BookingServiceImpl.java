package org.example.uberreviewservice.service;

import com.rideflow.rideflowentityservice.models.Booking;
import com.rideflow.rideflowentityservice.models.BookingStatus;
import com.rideflow.rideflowentityservice.models.Driver;
import com.rideflow.rideflowentityservice.models.Passenger;
import lombok.RequiredArgsConstructor;
import org.example.uberreviewservice.dto.booking.BookingRequestDTO;
import org.example.uberreviewservice.dto.booking.BookingResponseDTO;
import org.example.uberreviewservice.dto.booking.BookingStatusUpdateDTO;
import org.example.uberreviewservice.dto.review.ReviewSummaryDTO;
import org.example.uberreviewservice.exception.BookingNotFoundException;
import org.example.uberreviewservice.exception.InvalidBookingStatusTransitionException;
import org.example.uberreviewservice.exception.NoDriversAvailableException;
import org.example.uberreviewservice.exception.PassengerNotFoundException;
import org.example.uberreviewservice.mapper.BookingMapper;
import org.example.uberreviewservice.mapper.ReviewMapper;
import org.example.uberreviewservice.repository.BookingRepository;
import org.example.uberreviewservice.repository.DriverRepository;
import org.example.uberreviewservice.repository.PassengerRepository;
import org.example.uberreviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final ReviewRepository reviewRepository;

    // Defines the state machine: for each status, which statuses it can legally move to
    private static final Map<BookingStatus, EnumSet<BookingStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(BookingStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(BookingStatus.ASSIGNED_DRIVER,
                EnumSet.of(BookingStatus.CAB_ARRIVED, BookingStatus.CANCELED));
        ALLOWED_TRANSITIONS.put(BookingStatus.CAB_ARRIVED,
                EnumSet.of(BookingStatus.STARTED, BookingStatus.CANCELED));
        ALLOWED_TRANSITIONS.put(BookingStatus.STARTED,
                EnumSet.of(BookingStatus.IN_RIDE, BookingStatus.CANCELED));
        ALLOWED_TRANSITIONS.put(BookingStatus.IN_RIDE,
                EnumSet.of(BookingStatus.COMPLETED));
        ALLOWED_TRANSITIONS.put(BookingStatus.COMPLETED, EnumSet.noneOf(BookingStatus.class));
        ALLOWED_TRANSITIONS.put(BookingStatus.CANCELED, EnumSet.noneOf(BookingStatus.class));
    }

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        Passenger passenger = passengerRepository.findById(requestDTO.getPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException(requestDTO.getPassengerId()));

        Driver assignedDriver = findAvailableDriver();

        Booking booking = Booking.builder()
                .passenger(passenger)
                .driver(assignedDriver)
                .bookingStatus(BookingStatus.ASSIGNED_DRIVER)
                .totalDistance(0)
                .build();

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toResponseDTO(saved, null); // brand-new booking, no review possible yet
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        ReviewSummaryDTO reviewSummary = reviewRepository.findByBookingId(id)
                .map(ReviewMapper::toSummaryDTO)
                .orElse(null); // no review yet — perfectly valid

        return BookingMapper.toResponseDTO(booking, reviewSummary);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        // Fetch all reviews for these bookings in ONE query instead of N queries in a loop
        // (avoids the classic N+1 query problem)
        Map<Long, ReviewSummaryDTO> reviewsByBookingId = reviewRepository.findAll().stream()
                .filter(review -> review.getBooking() != null)
                .collect(Collectors.toMap(
                        review -> review.getBooking().getId(),
                        ReviewMapper::toSummaryDTO
                ));

        return bookings.stream()
                .map(booking -> BookingMapper.toResponseDTO(
                        booking, reviewsByBookingId.get(booking.getId())))
                .toList();
    }

    @Override
    public BookingResponseDTO updateStatus(Long id, BookingStatusUpdateDTO statusUpdateDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        BookingStatus current = booking.getBookingStatus();
        BookingStatus requested = statusUpdateDTO.getNewStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(BookingStatus.class))
                .contains(requested)) {
            throw new InvalidBookingStatusTransitionException(current.name(), requested.name());
        }

        booking.setBookingStatus(requested);

        if (requested == BookingStatus.STARTED) {
            booking.setStartTime(LocalTime.now());
        }
        if (requested == BookingStatus.COMPLETED) {
            booking.setEndTime(LocalTime.now());
            // totalDistance would normally come from a location-tracking service;
            // stubbed here since that model hasn't been shared yet
        }

        Booking updated = bookingRepository.save(booking);

        // Review can only exist once a ride is COMPLETED, so skip the extra query otherwise
        ReviewSummaryDTO reviewSummary = requested == BookingStatus.COMPLETED
                ? reviewRepository.findByBookingId(id).map(ReviewMapper::toSummaryDTO).orElse(null)
                : null;

        return BookingMapper.toResponseDTO(updated, reviewSummary);
    }

    private Driver findAvailableDriver() {
        // Placeholder for real matching logic (location proximity, availability status, etc.)
        return driverRepository.findAll().stream()
                .findFirst()
                .orElseThrow(NoDriversAvailableException::new);
    }
}