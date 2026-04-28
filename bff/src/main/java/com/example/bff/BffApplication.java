package com.example.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class BffApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffApplication.class, args);
	}

	//Cliente HTTP simple REST TEMPLATE - > NO USAR EN EVALUACION 2
	private RestTemplate restTemplate = new RestTemplate();
	//Para poder acceder desde el frontend al Backend
	@CrossOrigin(origins = "*") //Permite consultas de cualquier origen
	//Endpoint de bff
	@GetMapping("/saludo")
	public String obtenerSaludo(){

		String response = restTemplate.getForObject("http://localhost:8081/saludo", String.class);
		return "BFF dice" + respuesta;

	}


}
