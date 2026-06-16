package com.example.ms_lista_espera;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 🟢 Esta anotación sobrescribe la configuración SOLO para esta prueba
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.datasource.driverClassName=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.data.redis.repositories.enabled=false",
		"eureka.client.enabled=false" // 🟢 Desactiva Eureka para que no intente buscarlo
})
class MsListaEsperaApplicationTests {

	@Test
	void contextLoads() {
		// Si este test pasa, significa que Spring puede levantar el contexto
		// usando H2 sin depender de tu PostgreSQL real en Docker.
	}

}