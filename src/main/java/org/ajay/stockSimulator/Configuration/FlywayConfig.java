package org.ajay.stockSimulator.Configuration;



import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway() {
        FluentConfiguration config = Flyway.configure()
                .baselineOnMigrate(true)
                .failOnMissingLocations(false)   // ✅ ignore missing migration dirs
                .ignoreMigrationPatterns("*:pending") // ✅ prevent fail on pending
                .dataSource(
                        "jdbc:postgresql://localhost:5432/yourdb", // <-- replace
                        "username",
                        "password"
                );

        // 🚨 Flyway dropped setFailOnUnsupportedDatabase in 11.x
        // So we simply don't call it anymore.

        Flyway flyway = new Flyway(config);
        flyway.migrate();
        return flyway;
    }
}

