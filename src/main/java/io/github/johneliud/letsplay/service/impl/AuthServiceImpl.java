package io.github.johneliud.letsplay.service.impl;

import io.github.johneliud.letsplay.dto.auth.AuthResponse;
import io.github.johneliud.letsplay.dto.auth.LoginRequest;
import io.github.johneliud.letsplay.dto.auth.RegisterRequest;
import io.github.johneliud.letsplay.exception.DuplicateResourceException;
import io.github.johneliud.letsplay.exception.UnauthorizedException;
import io.github.johneliud.letsplay.model.User;
import io.github.johneliud.letsplay.repository.UserRepository;
import io.github.johneliud.letsplay.security.JwtUtil;
import io.github.johneliud.letsplay.service.AuthService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return getAuthResponse(savedUser);
    }

    @NonNull
    private AuthResponse getAuthResponse(User savedUser) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setRole(savedUser.getRole());
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        return getAuthResponse(user);
    }
}
