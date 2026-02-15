package org.ajay.stockSimulator;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableCaching
public class StockSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockSimulatorApplication.class, args);
	}


}
