package com.marcosperboni.angularbff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies the full Spring context wires up (bean dependencies, autoconfiguration, etc.)
 * without needing Redis/downstream services to actually be reachable at test time.
 */
@SpringBootTest
class AngularSpringBffPlatformApplicationTests {

    @Test
    void contextLoads() {
    }
}
