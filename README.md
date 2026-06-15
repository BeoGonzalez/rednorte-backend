# RedNorte Backend System

## Introducción
RedNorte Backend es una arquitectura de microservicios empresarial diseñada para la gestión clínica integral. El sistema implementa patrones de diseño modernos, incluyendo Backend For Frontend (BFF) para la orquestación de datos, una Arquitectura Orientada a Eventos (EDA) para la resiliencia entre servicios y seguridad centralizada mediante JWT.

## Arquitectura de Alto Nivel
El sistema se organiza en capas lógicas para garantizar la separación de responsabilidades y la escalabilidad independiente de cada componente:

* **API Gateway**: Punto único de entrada para el tráfico externo.
* **BFF (Backend For Frontend)**: Capa de agregación que orquesta llamadas a múltiples microservicios para reducir la latencia y complejidad en el lado del cliente.
* **Microservicios de Dominio**:
    * **ms-security**: Proveedor de identidad y gestión de autenticación.
    * **ms-lista-espera**: Lógica de negocio core y orquestación de citas.
    * **ms-pacientes & ms-doctores**: Gestión de datos persistentes con caché distribuida (Redis).
    * **ms-chatbot**: Integración con modelos de IA para triaje inteligente.
* **Infraestructura**:
    * **Service Discovery**: Netflix Eureka.
    * **Message Broker**: RabbitMQ (comunicación asíncrona mediante Spring Cloud Stream).
    * **Base de Datos**: PostgreSQL.
    * **Caché**: Redis.

## Stack Tecnológico
* **Framework**: Java 17+, Spring Boot 3, Spring Cloud.
* **Mensajería**: RabbitMQ (Spring Cloud Stream).
* **Caché**: Redis (Spring Data Redis).
* **Persistencia**: PostgreSQL, JPA/Hibernate.
* **Seguridad**: Spring Security, JWT (JSON Web Tokens).
* **Comunicación**: Feign Client (inter-service), RestTemplate.
* **Documentación**: OpenAPI/Swagger.
* **Containerización**: Docker & Docker Compose.

## Desglose de Componentes

| Módulo | Responsabilidad |
| :--- | :--- |
| `api-gateway` | Enrutamiento, filtrado y punto de entrada al clúster. |
| `bff` | Orquestación de servicios para la UI, agregación de respuestas. |
| `security` | Autenticación, emisión y validación de tokens JWT. |
| `ms-lista-espera` | Priorización algorítmica de citas y productor de eventos. |
| `ms-pacientes` | Gestión de fichas clínicas y consumidor de eventos. |
| `ms-doctores` | Gestión de perfiles y disponibilidad profesional. |
| `ms-chatbot` | Triaje inteligente basado en IA. |
| `eureka-server` | Registro y descubrimiento de servicios. |

## Comunicación Asíncrona
El sistema implementa una arquitectura orientada a eventos. Cuando una cita es asignada en `ms-lista-espera`, el sistema emite un evento a través de RabbitMQ. El microservicio `ms-pacientes` actúa como consumidor, permitiendo desacoplar la lógica de agendamiento de la lógica de notificaciones o actualización de estados del paciente, garantizando la consistencia eventual y mejorando la tolerancia a fallos.

## Configuración e Instalación

### Prerrequisitos
* Docker y Docker Compose.
* Java 17 o superior.
* Maven.

### Ejecución
Para levantar la infraestructura completa (PostgreSQL, Redis, RabbitMQ y Eureka), ejecute:

```bash
docker-compose up -d
