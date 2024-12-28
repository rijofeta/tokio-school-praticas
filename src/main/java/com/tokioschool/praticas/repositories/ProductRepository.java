package com.tokioschool.praticas.repositories;

import com.tokioschool.praticas.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Set<Product> findByNameContaining(String string);

    @Query(value = "SELECT * FROM products WHERE stock > 0", nativeQuery = true)
    Set<Product> findByInStock();

}
