package io.github.johneliud.letsplay.service.impl;

import io.github.johneliud.letsplay.dto.user.UpdateUserRequest;
import io.github.johneliud.letsplay.dto.user.UserResponse;
import io.github.johneliud.letsplay.model.User;
import io.github.johneliud.letsplay.repository.UserRepository;
import io.github.johneliud.letsplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.name != null) {
            user.setName(request.name);
        }
        if (request.email != null) {
            if (userRepository.existsByEmail(request.email) && !user.getEmail().equals(request.email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.email);
        }
        if (request.password != null) {
            user.setPassword(passwordEncoder.encode(request.password));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.role = user.getRole();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        return response;
    }
}
