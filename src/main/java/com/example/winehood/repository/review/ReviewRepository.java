package com.example.winehood.repository.review;

import com.example.winehood.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByWineId(Long wineId);
}
