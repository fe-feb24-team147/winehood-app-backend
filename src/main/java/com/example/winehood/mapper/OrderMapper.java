package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.order.OrderDto;
import com.example.winehood.model.Order;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);
}
