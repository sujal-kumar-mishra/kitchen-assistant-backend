package com.example.kitchen_assistant_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <-- AND ADD THIS ANNOTATION
public class KitchenAssistantBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(KitchenAssistantBackendApplication.class, args);
	}
}