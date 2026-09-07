package org.example.uberreviewservice.dto.review;

import lombok.*;

// Embedded inside BookingResponseDTO — deliberately minimal
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDTO {
    private Long id;
    private double rating;
    private String description;
}