package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(String id);
    ProductResponse createProduct(ProductRequest request, String userEmail);
    ProductResponse updateProduct(String id, ProductRequest request, String userEmail);
    void deleteProduct(String id, String userEmail);
    List<ProductResponse> getProductsByUser(String userId);
}
