package com.apnastore.service;

import com.apnastore.dto.AuthResponse;
import com.apnastore.dto.LoginRequest;
import com.apnastore.dto.RegisterRequest;
import com.apnastore.exception.BadRequestException;
import com.apnastore.model.Role;
import com.apnastore.model.User;
import com.apnastore.repository.UserRepository;
import com.apnastore.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Registration securely hashes user password before persisting")
    void register_HashesPasswordBeforeSaving() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Aarav Roy");
        request.setEmail("aarav@example.com");
        request.setPassword("PlainPass@2026");

        String encodedHash = "$2a$10$DUMMY_BCRYPT_HASH_VALUE";

        when(userRepository.existsByEmail("aarav@example.com")).thenReturn(false);
        when(passwordEncoder.encode("PlainPass@2026")).thenReturn(encodedHash);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken("aarav@example.com", "USER")).thenReturn("mock-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getPasswordHash()).isEqualTo(encodedHash);
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("PlainPass@2026");
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("aarav@example.com");
    }

    @Test
    @DisplayName("Registration rejects duplicate email addresses with BadRequestException")
    void register_WhenEmailAlreadyRegistered_ThrowsBadRequestException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Existing User");
        request.setEmail("existing@example.com");
        request.setPassword("Secret@123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Email is already registered: existing@example.com");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login fails with BadRequestException when password does not match")
    void login_WhenPasswordIsIncorrect_ThrowsBadRequestException() {
        // Arrange
        User existingUser = new User("Vikram", "vikram@example.com", "$2a$10$CORRECT_HASH", Role.USER);
        existingUser.setId(5L);

        when(userRepository.findByEmail("vikram@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$CORRECT_HASH")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("vikram@example.com");
        request.setPassword("WrongPassword");

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Invalid email or password");

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
