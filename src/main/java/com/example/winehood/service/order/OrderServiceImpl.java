package com.example.winehood.service.order;

import com.example.winehood.dto.order.CreateOrderRequestDto;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.dto.order.UpdateOrderRequestDto;
import com.example.winehood.dto.orderitem.OrderItemDto;
import com.example.winehood.exception.DataProcessingException;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.mapper.OrderItemMapper;
import com.example.winehood.mapper.OrderMapper;
import com.example.winehood.model.CartItem;
import com.example.winehood.model.Order;
import com.example.winehood.model.OrderItem;
import com.example.winehood.model.ShoppingCart;
import com.example.winehood.repository.order.OrderRepository;
import com.example.winehood.repository.orderitem.OrderItemRepository;
import com.example.winehood.repository.shoppingcart.ShoppingCartRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto createOrder(
            Long userId, Pageable pageable, CreateOrderRequestDto createOrderDto) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);

        validateCartItems(shoppingCart.getCartItems());

        Order order = createAndSaveOrder(shoppingCart, createOrderDto.shippingAddress());
        order.setOrderItems(createAndSaveOrderItems(order, shoppingCart.getCartItems()));

        clearShoppingCart(shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrders(Long userId, Pageable pageable) {
        List<Order> userOrders = orderRepository.findAllByUserId(userId, pageable);
        if (userOrders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for user with id: " + userId);
        }

        return orderMapper.toDtoList(userOrders);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderRequestDto updateOrderDto) {
        Order order = findOrderById(orderId);
        order.setStatus(updateOrderDto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId, Pageable pageable) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId, pageable);
        return orderItemMapper.toDtoList(orderItems);
    }

    @Override
    public OrderItemDto getOrderItemByIdAndOrderId(Long orderId, Long orderItemId) {
        OrderItem orderItem = findOrderItemById(orderItemId, orderId);
        validateOrderItemOwnership(orderItem, orderId);
        return orderItemMapper.toDto(orderItem);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user Id: " + userId));
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found for id: " + orderId));
    }

    private OrderItem findOrderItemById(Long orderItemId, Long orderId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found for id: " + orderId));
    }

    private void validateCartItems(Set<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new DataProcessingException(
                    "Unable to create order, add items to shopping cart.");
        }
    }

    private Order createAndSaveOrder(ShoppingCart shoppingCart, String shippingAddress) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setTotal(calculateTotalOrderPrice(shoppingCart.getCartItems()));
        return orderRepository.save(order);
    }

    private BigDecimal calculateTotalOrderPrice(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(c -> c.getWine().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> createAndSaveOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = cartItems.stream()
                .map(c -> {
                    OrderItem orderItem = orderItemMapper.toEntityFromCartItem(c);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());

        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    private void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    private void validateOrderItemOwnership(OrderItem orderItem, Long orderId) {
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new DataProcessingException(
                    "Order item does not belong to the order with id: " + orderId);
        }
    }
}
