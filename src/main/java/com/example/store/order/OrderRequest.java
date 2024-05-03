package com.example.store.order;

import com.example.store.product.Product;
import com.example.store.user.User;
import lombok.Data;

public class OrderRequest {

    @Data
    public static class SaveDTO{
        private Integer orderNum;
        private Integer priceSum;
        public Order toEntity(User user, Product product){
            return Order.builder()
                    .user(user)
                    .product(product)
                    .priceSum(priceSum)
                    .orderNum(orderNum)
                    .build();
        }
    }

}
