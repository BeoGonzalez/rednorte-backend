package com.example.ms_doctores;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // 🟢 Esto le dice a Spring que busque application-test.yml
class MsDoctoresApplicationTests {

	@Test
	void contextLoads() {
	}
}