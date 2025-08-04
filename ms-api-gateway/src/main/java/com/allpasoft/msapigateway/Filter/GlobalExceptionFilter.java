package com.allpasoft.msapigateway.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
public class GlobalExceptionFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(error -> {
                    logger.error("🚨 Global error handling: {}", error.getMessage(), error);
                    
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                    
                    String body = String.format(
                            "{\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor\",\"timestamp\":\"%d\",\"path\":\"%s\"}", 
                            System.currentTimeMillis(),
                            exchange.getRequest().getURI().getPath()
                    );
                    
                    var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                });
    }
}
