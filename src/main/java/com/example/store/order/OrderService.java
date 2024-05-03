package com.example.store.order;

import com.example.store.product.Product;
import com.example.store.product.ProductRepository;
import com.example.store.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse.DetailDTO orderSaveProduct(Integer productId, User user, OrderRequest.SaveDTO reqDTO){
        Product product = productRepository.findById(productId);
        if (product.getQty() < reqDTO.getOrderNum()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        product.setQty(product.getQty() - reqDTO.getOrderNum());
        productRepository.update(product);
        Order order = orderRepository.save(reqDTO.toEntity(user, product));
        order.setPriceSum(product.getPrice()*reqDTO.getOrderNum());
        return new OrderResponse.DetailDTO(order);
    }

    public OrderResponse.DetailDTO getOrderDetail(int id){
        Order order = orderRepository.findByProductId(id);
        return new OrderResponse.DetailDTO(order);
    }

    public List<OrderResponse.ListDTO> getOrderList(int userId){
        List<Order> orderList = orderRepository.findProductByUserId(userId);
        return orderList.stream().map(OrderResponse.ListDTO::new).collect(Collectors.toList());
    }
}
