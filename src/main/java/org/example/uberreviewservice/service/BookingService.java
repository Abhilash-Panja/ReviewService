package org.example.uberreviewservice.service;

import org.example.uberreviewservice.dto.booking.BookingRequestDTO;
import org.example.uberreviewservice.dto.booking.BookingResponseDTO;
import org.example.uberreviewservice.dto.booking.BookingStatusUpdateDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);
    BookingResponseDTO getBooking(Long id);
    List<BookingResponseDTO> getAllBookings();
    BookingResponseDTO updateStatus(Long id, BookingStatusUpdateDTO statusUpdateDTO);
}