package com.tokioschool.praticas.services;

import com.tokioschool.praticas.domain.Product;
import com.tokioschool.praticas.exceptions.ProductNotFoundException;
import com.tokioschool.praticas.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Set<Product> findByNameContainig(String string) {
        return productRepository.findByNameContaining(string);
    }

    public Set<Product> findByInStock() {
        return productRepository.findByInStock();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new ProductNotFoundException(product.getId());
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public Product findById(Long id) {
        try {
            return productRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new ProductNotFoundException(id);
        }
    }
}
