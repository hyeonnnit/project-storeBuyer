package com.example.store.order;

import lombok.Data;

public class OrderResponse {
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
