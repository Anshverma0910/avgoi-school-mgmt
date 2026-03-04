package com.avgoi.schoolmgmt.controller;

import com.avgoi.schoolmgmt.security.CustomUserDetails;
import com.avgoi.schoolmgmt.security.JwtProvider;
import com.avgoi.schoolmgmt.entity.User;
import com.avgoi.schoolmgmt.repository.UserRepository;
import com.avgoi.schoolmgmt.service.RefreshTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.allow-password-reset:false}")
    private boolean allowPasswordReset;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String accessToken = jwtProvider.generateToken(userDetails);
        String refreshToken = refreshTokenService.createAndSave(userDetails.getUser());
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "token", accessToken,
                "email", userDetails.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@Valid @RequestBody RefreshRequest request) {
        return refreshTokenService.findUserByValidToken(request.getRefreshToken())
                .flatMap(user -> userRepository.findByIdWithRoleAndPermissions(user.getId()))
                .map(user -> {
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    String accessToken = jwtProvider.generateToken(userDetails);
                    return ResponseEntity.<Map<String, String>>ok(Map.of("accessToken", accessToken, "token", accessToken));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token")));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody LogoutRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Dev only: reset password for an existing user (enable with app.allow-password-reset=true).
     * Use POST with body: { "email": "ansh@avgoi.com", "newPassword": "password123" }
     */
    @PostMapping(value = "/reset-password", consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!allowPasswordReset) {
            return ResponseEntity.status(403).body(Map.of("error", "Password reset is disabled"));
        }
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated for " + request.getEmail()));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPasswordGet() {
        return ResponseEntity.status(405).body(Map.of(
                "error", "Method Not Allowed",
                "message", "Use POST with body: {\"email\": \"your@email.com\", \"newPassword\": \"newpass\"}"));
    }

    @lombok.Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @lombok.Data
    public static class ResetPasswordRequest {
        private String email;
        private String newPassword;
    }

    @lombok.Data
    public static class RefreshRequest {
        @NotBlank(message = "refreshToken is required")
        private String refreshToken;
    }

    @lombok.Data
    public static class LogoutRequest {
        @NotBlank(message = "refreshToken is required")
        private String refreshToken;
    }
}
