package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(String id);
    ProductResponse createProduct(ProductRequest request, String userId);
    ProductResponse updateProduct(String id, ProductRequest request, String userId);
    void deleteProduct(String id, String userId);
    List<ProductResponse> getProductsByUser(String userId);
}
