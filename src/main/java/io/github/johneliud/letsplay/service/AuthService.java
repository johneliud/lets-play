package io.github.johneliud.letsplay.service;

import io.github.johneliud.letsplay.dto.auth.AuthResponse;
import io.github.johneliud.letsplay.dto.auth.LoginRequest;
import io.github.johneliud.letsplay.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
