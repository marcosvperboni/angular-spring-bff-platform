package com.marcosperboni.angularbff.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("test-secret-key-for-jwt-unit-tests-min-32-bytes", 60);

    @Test
    void generatesTokenThatValidatesAndReturnsTheOriginalUsername() {
        String token = jwtService.generateToken("admin");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void rejectsAGarbageToken() {
        assertThat(jwtService.isValid("not-a-real-token")).isFalse();
    }
}
