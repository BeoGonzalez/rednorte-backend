package com.example.ms_lista_espera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsListaEsperaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsListaEsperaApplication.class, args);
	}

}
