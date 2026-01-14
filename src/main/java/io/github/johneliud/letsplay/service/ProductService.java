package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> searchProducts(String name, Double minPrice, Double maxPrice, Pageable pageable);
    ProductResponse getProductById(String id);
    ProductResponse createProduct(ProductRequest request, String userEmail);
    ProductResponse updateProduct(String id, ProductRequest request, String userEmail);
    void deleteProduct(String id, String userEmail);
    List<ProductResponse> getProductsByUser(String userId);
}
