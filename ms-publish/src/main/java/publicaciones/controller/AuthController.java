package publicaciones.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import publicaciones.config.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        String token = JwtUtil.generateToken(
                username,
                Map.of("role", "USER")
        );

        return ResponseEntity.ok(Map.of("token", token));
    }
}
