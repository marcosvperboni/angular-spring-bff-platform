package com.marcosperboni.angularbff.controller;

import com.marcosperboni.angularbff.dto.LoginRequest;
import com.marcosperboni.angularbff.dto.LoginResponse;
import com.marcosperboni.angularbff.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEMO_USERNAME = "admin";
    // Demo-only in-memory user; encoded once at class load so the plaintext "admin123" never sits in a field.
    private static final PasswordEncoder BOOTSTRAP_ENCODER = new BCryptPasswordEncoder();
    private static final String DEMO_PASSWORD_HASH = BOOTSTRAP_ENCODER.encode("admin123");

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        boolean valid = DEMO_USERNAME.equals(request.username())
                && passwordEncoder.matches(request.password(), DEMO_PASSWORD_HASH);
        if (!valid) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        }
        return Mono.just(new LoginResponse(jwtService.generateToken(request.username())));
    }
}
