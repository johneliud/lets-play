package io.github.johneliud.letsplay.service.impl;

import io.github.johneliud.letsplay.dto.product.ProductRequest;
import io.github.johneliud.letsplay.dto.product.ProductResponse;
import io.github.johneliud.letsplay.exception.ForbiddenException;
import io.github.johneliud.letsplay.exception.ResourceNotFoundException;
import io.github.johneliud.letsplay.model.Product;
import io.github.johneliud.letsplay.model.User;
import io.github.johneliud.letsplay.repository.ProductRepository;
import io.github.johneliud.letsplay.repository.UserRepository;
import io.github.johneliud.letsplay.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String name, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.searchProducts(name, minPrice, maxPrice, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(user.getId());

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (canModifyProduct(product, user.getId())) {
            throw new ForbiddenException("Access denied: You can only modify your own products");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (canModifyProduct(product, user.getId())) {
            throw new ForbiddenException("Access denied: You can only delete your own products");
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
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

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