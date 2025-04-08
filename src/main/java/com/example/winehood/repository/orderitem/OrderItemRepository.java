package com.example.winehood.repository.orderitem;

import com.example.winehood.model.OrderItem;
import com.example.winehood.model.Region;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>,
        JpaSpecificationExecutor<Region> {
    List<OrderItem> findAllByOrderId(Long orderId, Pageable pageable);
}
