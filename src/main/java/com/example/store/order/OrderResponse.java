package com.example.store.order;

import com.example.store.product.Product;
import com.example.store.user.User;
import lombok.Data;

public class OrderResponse {
    @Data
    public static class DetailDTO{
        private int id;
        private User user;
        private Product product;
        private String name;
        private int price ;
        private int qty;
        private String pic;
        private Integer orderNum;
        private Integer priceSum;



        public DetailDTO(Order order){
            this.id = order.getProduct().getId();
            this.user=order.getUser();
            this.product=order.getProduct();
            this.orderNum = order.getOrderNum();
            this.priceSum = order.getPriceSum();
            this.name = order.getProduct().getName();
            this.price = order.getProduct().getPrice();
            this.qty = order.getProduct().getQty();
            this.pic = order.getProduct().getPic();
        }
    }

    @Data
    public static class ListDTO{
        private int id;
        private String name;
        private int price ;
        private int qty;
        private String pic;
        private Integer orderNum;
        private Integer priceSum;

        public ListDTO(Order order){
            this.id = order.getProduct().getId();
            this.orderNum = order.getOrderNum();
            this.priceSum = order.getPriceSum();
            this.name = order.getProduct().getName();
            this.price = order.getProduct().getPrice();
            this.qty = order.getProduct().getQty();
            this.pic = order.getProduct().getPic();
        }
    }
}
