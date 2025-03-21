package com.example.winehood.repository.order;

import com.example.winehood.model.Order;
import com.example.winehood.model.Region;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Region> {
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
