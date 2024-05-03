package com.example.store.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public Order save(Order order){
        em.persist(order);
        return order;
    }

    public Order findByProductId(int id) {
        Query query = em.createQuery("select o from Order o JOIN FETCH o.product p WHERE p.id =:id");
        query.setParameter("id", id);
        return (Order) query.getSingleResult();
    }

    public List<Order> findProductByUserId(int userId) {
        Query query =
                em.createQuery("select o from Order o JOIN FETCH o.product p JOIN FETCH o.user u WHERE u.id = :user_id", Order.class);
        query.setParameter("user_id", userId);
        return query.getResultList();
    }
}
