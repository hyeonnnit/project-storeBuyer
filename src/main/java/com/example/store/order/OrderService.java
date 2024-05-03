package com.example.store.order;

import com.example.store.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderResponse.DetailDTO getOrderDetail(int id){
        Order order = orderRepository.findByProductId(id);
        return new OrderResponse.DetailDTO(order);
    }

    public List<OrderResponse.ListDTO> getOrderList(int userId){
        List<Order> orderList = orderRepository.findProductByUserId(userId);
        return orderList.stream().map(OrderResponse.ListDTO::new).collect(Collectors.toList());
    }
}
