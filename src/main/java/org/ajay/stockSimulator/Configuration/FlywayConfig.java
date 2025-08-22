package org.ajay.stockSimulator.Configuration;



import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.flywaydb.core.Flyway;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties properties) {
        return Flyway.configure()
                .dataSource(properties.getUrl(), properties.getUser(), properties.getPassword())
                .ignoreMigrationPatterns("*:pending")   // ignores some errors
                .validateOnMigrate(false)               // skip validation
                .baselineOnMigrate(true)                // allows running on existing DB
                .load();
    }
}

