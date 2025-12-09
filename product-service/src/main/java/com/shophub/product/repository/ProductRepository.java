package com.shophub.product.repository;

import com.shophub.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Recherche par catégorie
    List<Product> findByCategory(String category);
    
    // Recherche par nom (contient)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Produits en stock
    List<Product> findByStockGreaterThan(Integer stock);
}