package org.ajay.stockSimulator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // This tells Spring Boot to use application-test.properties
class StockSimulatorApplicationTests {

    @Test
    void contextLoads() {
        // This is a basic smoke test to make sure the Spring context starts up
    }
}
