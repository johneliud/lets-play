package io.github.johneliud.letsplay.controller;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import io.github.johneliud.letsplay.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        
        // If pagination parameters provided, return paginated results
        if (page != null && size != null) {
            Sort sort = sortBy != null ? Sort.by(sortBy) : Sort.by("createdAt").descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // If search parameters provided, use search
            if (name != null || minPrice != null || maxPrice != null) {
                Page<ProductResponse> products = productService.searchProducts(name, minPrice, maxPrice, pageable);
                return ResponseEntity.ok(products);
            }
            
            Page<ProductResponse> products = productService.getAllProducts(pageable);
            return ResponseEntity.ok(products);
        }
        
        // Default: return all products without pagination
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