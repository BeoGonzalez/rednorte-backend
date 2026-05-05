package com.example.ms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MsClientApplication {
	//Instancia de WebClient
	private final WebClient webClient = WebClient.create("http://localhost:8081");

	public static void main(String[] args) {
		SpringApplication.run(MsClientApplication.class, args);
	}

}
