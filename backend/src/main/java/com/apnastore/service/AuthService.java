package com.apnastore.service;

import com.apnastore.dto.AuthResponse;
import com.apnastore.dto.LoginRequest;
import com.apnastore.dto.RegisterRequest;
import com.apnastore.exception.BadRequestException;
import com.apnastore.model.Role;
import com.apnastore.model.User;
import com.apnastore.repository.UserRepository;
import com.apnastore.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user with the USER role.
     * Throws BadRequestException if the email is already in use.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        String hash = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getName(), request.getEmail(), hash, Role.USER);
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    /**
     * Authenticate a user and return a JWT.
     * Throws BadRequestException for bad credentials (generic message to avoid user enumeration).
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
