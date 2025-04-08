package com.example.winehood.dto.review;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        Long wineId,
        Long userId,
        String text,
        LocalDateTime timestamp) {
}
