package com.shophub.product.service;

import com.shophub.product.entity.Product;
import com.shophub.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // Récupérer tous les produits
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Récupérer un produit par ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Créer un nouveau produit
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    // Mettre à jour un produit
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        
        return productRepository.save(product);
    }
    
    // Supprimer un produit
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    // Rechercher par catégorie
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Rechercher par nom
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Vérifier le stock
    public boolean checkStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getStock() >= quantity;
    }
    
    // Réduire le stock (pour les commandes)
    public void reduceStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}