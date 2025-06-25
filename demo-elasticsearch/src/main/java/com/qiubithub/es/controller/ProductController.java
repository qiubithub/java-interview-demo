package com.qiubithub.es.controller;

import com.qiubithub.es.model.Product;
import com.qiubithub.es.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> findByName(@RequestParam String name) {
        List<Product> products = productService.findByName(name);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/keyword")
    public ResponseEntity<List<Product>> findByNameContaining(@RequestParam String keyword) {
        List<Product> products = productService.findByNameContaining(keyword);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/category")
    public ResponseEntity<List<Product>> findByCategory(@RequestParam String category) {
        List<Product> products = productService.findByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/price")
    public ResponseEntity<List<Product>> findByPriceRange(
            @RequestParam double minPrice, 
            @RequestParam double maxPrice) {
        List<Product> products = productService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/brand")
    public ResponseEntity<List<Product>> findByBrand(@RequestParam String brand) {
        List<Product> products = productService.findByBrand(brand);
        return ResponseEntity.ok(products);
    }
}