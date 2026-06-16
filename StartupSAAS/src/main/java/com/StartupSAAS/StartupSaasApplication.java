package com.StartupSAAS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StartupSaasApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartupSaasApplication.class, args);
	}
}
