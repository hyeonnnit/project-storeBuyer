package com.example.store.order;

import com.example.store.product.Product;
import com.example.store.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Table(name = "order_tb")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer orderNum;

    @Column
    private Integer priceSum;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Order(Integer id, User user, Product product, Integer priceSum, Integer orderNum, LocalDateTime createdAt) {
        this.id = id;
        this.orderNum = orderNum;
        this.priceSum = priceSum;
        this.user = user;
        this.product = product;
        this.createdAt = createdAt;
    }
}
