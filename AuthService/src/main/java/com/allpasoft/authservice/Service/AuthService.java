package com.allpasoft.authservice.Service;

import com.allpasoft.authservice.Dto.*;
import com.allpasoft.authservice.Entity.User;
import com.allpasoft.authservice.JwtUtils.JwtUtils;
import com.allpasoft.authservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        
        userRepository.save(user);
        
        // Generar token
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername());
    }
    
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        // Generar token
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername());
    }
    
    public TokenValidationResponse validateToken(String token) {
        try {
            if (!jwtUtils.isTokenValid(token)) {
                return new TokenValidationResponse(false, null, null);
            }
            
            String username = jwtUtils.getUsernameFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            
            // Verificar que el usuario aún existe en la base de datos
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            
            if (user == null) {
                return new TokenValidationResponse(false, null, null);
            }
            
            return new TokenValidationResponse(true, username, role != null ? role : user.getRole());
        } catch (Exception e) {
            return new TokenValidationResponse(false, null, null);
        }
    }
    
    public UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return new UserInfoResponse(user.getUsername(), user.getRole(), user.getEmail());
    }
}
