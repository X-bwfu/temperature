package com.example.aitemperaturebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AiTemperatureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiTemperatureBackendApplication.class, args);
    }

}
