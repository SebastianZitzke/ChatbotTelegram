package com.example.botTurnos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BarberiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberiaApplication.class, args);
	}

	// Este endpoint es para que Render sepa que tu app está "viva"
	@GetMapping("/")
	public String healthCheck() {
		return "BarberBot is alive!";
	}
}
