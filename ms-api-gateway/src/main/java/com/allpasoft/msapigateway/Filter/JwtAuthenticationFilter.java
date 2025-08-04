package com.allpasoft.msapigateway.Filter;

import com.allpasoft.msapigateway.Service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Value("${app.jwt.secret:una_clave_muy_segura_1234567890123456}")
    private String secretKey;
    
    @Value("${app.auth-service.url:http://localhost:8080}")
    private String authServiceUrl;
    
    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();
        
        logger.debug("🔍 Filtering request: {} {}", method, path);
        
        // Rutas públicas que no requieren autenticación
        if (isPublicPath(path)) {
            logger.debug("✅ Public path allowed: {}", path);
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("❌ No valid authorization header for path: {}", path);
            return unauthorized(exchange, "Token requerido para acceder a este recurso");
        }

        String token = authHeader.substring(7);

        // Validación local del token JWT
        return validateTokenLocally(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        return enrichRequestWithUserInfo(exchange, token)
                                .flatMap(enrichedExchange -> chain.filter(enrichedExchange));
                    } else {
                        // Si la validación local falla, intentar validación remota
                        return validateTokenRemotely(exchange, token, chain);
                    }
                })
                .onErrorResume(error -> {
                    logger.error("❌ Error processing token: {}", error.getMessage());
                    return unauthorized(exchange, "Error interno de autenticación");
                });
    }

    /**
     * Valida el token JWT localmente
     */
    private Mono<Boolean> validateTokenLocally(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                
                logger.debug("✅ Local JWT validation successful for user: {}", claims.getSubject());
                return true;
            } catch (Exception e) {
                logger.debug("⚠️ Local JWT validation failed: {}", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Valida el token remotamente contra el servicio de autenticación
     */
    private Mono<Void> validateTokenRemotely(ServerWebExchange exchange, String token, GatewayFilterChain chain) {
        return authService.validateToken(token)
                .flatMap(response -> {
                    if (response.isValid()) {
                        logger.debug("✅ Remote token validation successful for user: {}", response.getUsername());
                        
                        // Enriquecer el request con información del usuario
                        ServerWebExchange enrichedExchange = exchange.mutate()
                                .request(r -> r.header("X-User", response.getUsername())
                                              .header("X-User-Role", response.getRole())
                                              .header("X-Token-Valid", "true"))
                                .build();
                        
                        return chain.filter(enrichedExchange);
                    } else {
                        logger.debug("❌ Remote token validation failed");
                        return unauthorized(exchange, "Token inválido o expirado");
                    }
                })
                .onErrorResume(error -> {
                    logger.error("❌ Error validating token remotely: {}", error.getMessage());
                    return unauthorized(exchange, "Error validando token con el servicio de autenticación");
                });
    }

    /**
     * Enriquece el request con información del usuario extraída del token local
     */
    private Mono<ServerWebExchange> enrichRequestWithUserInfo(ServerWebExchange exchange, String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                
                return exchange.mutate()
                        .request(r -> r.header("X-User", username)
                                      .header("X-User-Role", role != null ? role : "USER")
                                      .header("X-Token-Valid", "true"))
                        .build();
            } catch (Exception e) {
                logger.error("❌ Error extracting user info from token: {}", e.getMessage());
                return exchange;
            }
        });
    }

    /**
     * Respuesta de no autorizado
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        logger.debug("🚫 Unauthorized access to: {} - {}", exchange.getRequest().getURI().getPath(), message);
        
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        
        String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%d\",\"path\":\"%s\"}", 
                message, 
                System.currentTimeMillis(),
                exchange.getRequest().getURI().getPath());
        
        var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * Redirección al servicio de autenticación (para usar en casos específicos)
     */
    @SuppressWarnings("unused")
    private Mono<Void> redirectToAuth(ServerWebExchange exchange) {
        logger.debug("� Redirecting to auth service from: {}", exchange.getRequest().getURI().getPath());
        
        String redirectUrl = authServiceUrl + "/auth/login?redirect=" + 
                           exchange.getRequest().getURI().toString();
        
        exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
        return exchange.getResponse().setComplete();
    }

    /**
     * Determina si una ruta es pública
     */
    private boolean isPublicPath(String path) {
        boolean isPublic = path.startsWith("/actuator") ||
               path.startsWith("/auth/") ||          // Todas las rutas de autenticación
               path.equals("/") ||
               path.startsWith("/webjars") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/error") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/health") ||
               path.matches("^/auth/.*");            // Regex para asegurar todas las rutas de auth
        
        if (isPublic) {
            logger.debug("🟢 Path {} is public", path);
        } else {
            logger.debug("🔒 Path {} requires authentication", path);
        }
        
        return isPublic;
    }

    @Override
    public int getOrder() {
        // Orden más alto para ejecutarse antes que otros filtros
        return -100;
    }
}
