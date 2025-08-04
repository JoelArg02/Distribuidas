package com.allpasoft.msapigateway.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Value("${app.auth-service.url:http://SERVICIO-AUTH}")
    private String authServiceUrl;
    
    private final WebClient webClient;
    
    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
    
    /**
     * Valida un token JWT contra el servicio de autenticación
     */
    public Mono<TokenValidationResponse> validateToken(String token) {
        logger.debug("🔍 Validating token with AuthService");
        
        return webClient.post()
                .uri(authServiceUrl + "/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(response -> logger.debug("✅ Token validated successfully for user: {}", response.getUsername()))
                .doOnError(error -> logger.debug("❌ Token validation failed: {}", error.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.just(new TokenValidationResponse(false, null, null));
                    }
                    return Mono.just(new TokenValidationResponse(false, null, null));
                })
                .onErrorReturn(new TokenValidationResponse(false, null, null));
    }
    
    /**
     * Obtiene información del usuario basado en el token
     */
    public Mono<UserInfoResponse> getUserInfo(String username) {
        logger.debug("👤 Getting user info for: {}", username);
        
        return webClient.get()
                .uri(authServiceUrl + "/auth/user/{username}", username)
                .retrieve()
                .bodyToMono(UserInfoResponse.class)
                .timeout(Duration.ofSeconds(3))
                .doOnSuccess(response -> logger.debug("✅ User info retrieved for: {}", username))
                .doOnError(error -> logger.debug("❌ Failed to get user info: {}", error.getMessage()))
                .onErrorReturn(new UserInfoResponse(username, "USER"));
    }
    
    /**
     * Respuesta de validación de token
     */
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
        private String role;
        
        public TokenValidationResponse() {}
        
        public TokenValidationResponse(boolean valid, String username, String role) {
            this.valid = valid;
            this.username = username;
            this.role = role;
        }
        
        // Getters y setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    /**
     * Respuesta de información del usuario
     */
    public static class UserInfoResponse {
        private String username;
        private String role;
        
        public UserInfoResponse() {}
        
        public UserInfoResponse(String username, String role) {
            this.username = username;
            this.role = role;
        }
        
        // Getters y setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
