package org.example.uberreviewservice.dto.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;      // e.g. "NOT_FOUND"
    private String message;
    private String path;       // request URI, helpful for debugging
}