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
        product.setName(request.name);
        product.setDescription(request.description);
        product.setPrice(request.price);
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

        product.setName(request.name);
        product.setDescription(request.description);
        product.setPrice(request.price);

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
        response.id = product.getId();
        response.name = product.getName();
        response.description = product.getDescription();
        response.price = product.getPrice();
        response.userId = product.getUserId();
        response.createdAt = product.getCreatedAt();
        response.updatedAt = product.getUpdatedAt();

        // Get user name
        userRepository.findById(product.getUserId())
            .ifPresent(user -> response.userName = user.getName());

        return response;
    }
}
