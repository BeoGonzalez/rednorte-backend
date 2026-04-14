package com.example.ms_lista_espera.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Timeout máximo para establecer conexión con el otro microservicio
        factory.setConnectTimeout(3000);

        // Timeout máximo de espera para recibir respuesta
        factory.setReadTimeout(3000);

        return new RestTemplate(factory);
    }
}