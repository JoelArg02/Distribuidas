package com.allpasoft.authservice.Controller;

import com.allpasoft.authservice.Dto.TokenResponse;
import com.allpasoft.authservice.JwtUtils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutentificationController {
    private final JwtUtils jwtUtils;

    public AutentificationController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/token")
    public ResponseEntity<TokenResponse> generateToken() {
        String token = jwtUtils.generateToken();
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
