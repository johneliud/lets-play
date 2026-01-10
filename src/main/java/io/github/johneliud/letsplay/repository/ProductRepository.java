package io.github.johneliud.letsplay.repository;

import io.github.johneliud.letsplay.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserId(String userId);

    List<Product> findAllByUserId(String userId);

}
