package com.allpasoft.authservice.Controller;

import com.allpasoft.authservice.Dto.*;
import com.allpasoft.authservice.JwtUtils.JwtUtils;
import com.allpasoft.authservice.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AuthService authService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.info("🏥 Health check called");
        return ResponseEntity.ok("AuthService is running!");
    }
    
    @GetMapping("/token")
    public ResponseEntity<TokenResponse> generateToken() {
        logger.info("🎫 Generating token");
        String token = jwtUtils.generateToken();
        return ResponseEntity.ok(new TokenResponse(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.info("📝 Register request for user: {}", request.getUsername());
        try {
            AuthResponse response = authService.register(request);
            logger.info("✅ User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("🔐 Login request for user: {}", request.getUsername());
        try {
            AuthResponse response = authService.login(request);
            logger.info("✅ User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Login failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("🔍 Token validation request");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("❌ Invalid authorization header");
            return ResponseEntity.badRequest().body(new TokenValidationResponse(false, null, null));
        }
        
        String token = authHeader.substring(7);
        
        try {
            TokenValidationResponse response = authService.validateToken(token);
            logger.info("✅ Token validation successful for user: {}", response.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Token validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new TokenValidationResponse(false, null, e.getMessage()));
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        logger.info("👤 User info request for: {}", username);
        try {
            UserInfoResponse response = authService.getUserInfo(username);
            logger.info("✅ User info retrieved for: {}", username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Failed to get user info for {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
