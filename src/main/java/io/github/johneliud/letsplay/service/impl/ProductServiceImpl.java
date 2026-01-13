package io.github.johneliud.letsplay.service.impl;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import io.github.johneliud.letsplay.model.Product;
import io.github.johneliud.letsplay.model.User;
import io.github.johneliud.letsplay.repository.ProductRepository;
import io.github.johneliud.letsplay.repository.UserRepository;
import io.github.johneliud.letsplay.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, String userId) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(userId);

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request, String userId) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!canModifyProduct(product, userId)) {
            throw new RuntimeException("Access denied");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id, String userId) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!canModifyProduct(product, userId)) {
            throw new RuntimeException("Access denied");
        }

        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getProductsByUser(String userId) {
        return productRepository.findByUserId(userId).stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
    }

    private boolean canModifyProduct(Product product, String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        return isAdmin || product.getUserId().equals(userId);
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setUserId(product.getUserId());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        // Get user name
        userRepository.findById(product.getUserId())
            .ifPresent(user -> response.setUserName(user.getName()));

        return response;
    }
}
