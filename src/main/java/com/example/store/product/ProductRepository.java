package com.example.store.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;

    @Transactional
    public void update(Product product) {
        em.merge(product);
    }

    public Product findById(int id) {
        Product product = em.find(Product.class, id);
        return product;
    }

    public List<Product> findAll() {
        Query query =
                em.createQuery("select p from Product p order by p.id desc", Product.class);
        return query.getResultList();
    }
}
