package io.github.johneliud.letsplay.controller;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import io.github.johneliud.letsplay.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        String userEmail = auth.getName();
        ProductResponse product = productService.createProduct(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        String userEmail = auth.getName();
        ProductResponse product = productService.updateProduct(id, request, userEmail);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        String userEmail = auth.getName();
        productService.deleteProduct(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}