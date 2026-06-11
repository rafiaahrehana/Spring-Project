package com.saas.luminex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LuminexApplication {
    public static void main(String[] args) {
        SpringApplication.run(LuminexApplication.class, args);
    }
}
