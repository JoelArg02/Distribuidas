package com.allpasoft.msapigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GatewayController {

    @GetMapping("/")
    public Mono<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "API Gateway");
        response.put("status", "UP");
        response.put("message", "API Gateway está funcionando correctamente");
        
        response.put("available_routes", List.of(
            "/publicaciones/** -> MS-PUBLISH",
            "/catalogo/** -> MS-CATALOGO", 
            "/notificaciones/** -> NOTIFICACIONES",
            "/auth/** -> SERVICIO-AUTH",
            "/actuator/** -> Gateway Actuator endpoints"
        ));
        
        return Mono.just(response);
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        return Mono.just(response);
    }
}
