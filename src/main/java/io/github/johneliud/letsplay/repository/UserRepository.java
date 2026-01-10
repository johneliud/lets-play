package io.github.johneliud.letsplay.repository;

import io.github.johneliud.letsplay.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User , String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
