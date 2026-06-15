package com.example.pacientes.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    //Registrando a cacheManager dentro de Spring con el Bean
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //Creamos una configuración base para la cahe de redis
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //Definimos expiracion de la caché en 10 min
                .entryTtl(Duration.ofMinutes(10));
        //Construir  devolver el admin de caché
        return RedisCacheManager.builder(connectionFactory)
                //aplicar esta configuracion por default a todos los caché
                .cacheDefaults(config).build();
    }

}