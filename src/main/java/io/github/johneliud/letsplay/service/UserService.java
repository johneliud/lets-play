package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.user.UpdateUserRequest;
import io.github.johneliud.letsplay.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(String id);
    UserResponse updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
}
