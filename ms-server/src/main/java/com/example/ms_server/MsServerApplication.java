package com.example.ms_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
public class MsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsServerApplication.class, args);
	}


	//WebClient = Comunicacion entre MS
	//Cuando usarlo = cuando un ms necesita datos de otro ms

	@GetMapping("/saludo")
	public String saludo() {
		return "Hola un saludo desde el MS 8081 SERVER";
	}

	@GetMapping("/saludo/{nombre}")
	public String saludoPersonalizado(@PathVariable String nombre){
		return "Hola " + nombre + "Desde el MS 8081 SERVER";
	}
}
