package io.github.johneliud.letsplay.service.impl;

import io.github.johneliud.letsplay.dto.auth.AuthResponse;
import io.github.johneliud.letsplay.dto.auth.LoginRequest;
import io.github.johneliud.letsplay.dto.auth.RegisterRequest;
import io.github.johneliud.letsplay.model.User;
import io.github.johneliud.letsplay.repository.UserRepository;
import io.github.johneliud.letsplay.security.JwtUtil;
import io.github.johneliud.letsplay.service.AuthService;
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
        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email, request.password)
        );

        User user = userRepository.findByEmail(request.email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }
}
