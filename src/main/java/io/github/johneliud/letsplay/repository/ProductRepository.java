package io.github.johneliud.letsplay.repository;

import io.github.johneliud.letsplay.model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserId(String userId);

    Page<Product> findAll(@NonNull Pageable pageable);

    @Query("{ $and: [ " +
            "{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { ?0: null } ] }, " +
            "{ $or: [ { 'price': { $gte: ?1 } }, { ?1: null } ] }, " +
            "{ $or: [ { 'price': { $lte: ?2 } }, { ?2: null } ] } " +
            "] }")
    Page<Product> searchProducts(String name, Double minPrice, Double maxPrice, Pageable pageable);
}