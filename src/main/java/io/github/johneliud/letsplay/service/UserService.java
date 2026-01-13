package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.user.UpdateUserRequest;
import io.github.johneliud.letsplay.dto.user.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(String id);
    UserResponse updateUser(String id, UpdateUserRequest request);
    void deleteUser(String id);
}
