package com.example.winehood.service.order;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId,
                         Pageable pageable,
                         CreateOrderRequestDto createOrderDto);

    List<OrderDto> getOrders(Long userId,
                             Pageable pageable);

    OrderDto updateOrderStatus(Long orderId,
                               UpdateOrderRequestDto updateOrderDto);

    List<OrderItemDto> getOrderItemsByOrderId(Long orderId,
                                              Pageable pageable);

    OrderItemDto getOrderItemByIdAndOrderId(Long orderId,
                                            Long orderItemId);
}
