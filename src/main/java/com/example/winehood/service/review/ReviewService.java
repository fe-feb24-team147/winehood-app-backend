package com.example.winehood.service.review;

import com.example.winehood.dto.review.CreateReviewRequestDto;
import com.example.winehood.dto.review.ReviewDto;
import com.example.winehood.model.User;
import java.util.List;

public interface ReviewService {
    ReviewDto save(User user, CreateReviewRequestDto requestDto);

    List<ReviewDto> findAllReviewsByWineId(Long wineId);
}
