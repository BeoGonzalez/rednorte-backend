# RedNorte Healthcare Platform: Backend Core System

## 1. Introducción y Contexto Institucional
El **Servicio Público de Salud RedNorte** administra una red de establecimientos asistenciales que enfrentan desafíos críticos en la gestión de listas de espera para consultas médicas, procedimientos y cirugías. La falta de integración entre los sistemas actuales genera ineficiencias operativas, pérdida de horas médicas por cancelaciones de último momento y una escasa visibilidad de las solicitudes para los pacientes.

Esta plataforma representa una solución tecnológica centralizada basada en una **arquitectura de microservicios escalable**, diseñada para optimizar la asignación de citas y garantizar la transparencia y seguridad en el manejo de datos críticos de salud.

---

## 2. Arquitectura de Microservicios
El sistema se fundamenta en un diseño modular y desacoplado, permitiendo que cada componente evolucione de manera independiente mediante el uso de arquetipos **Maven** personalizados.

### Componentes Core del Backend
Siguiendo los requerimientos técnicos de la asignatura **DSY1106**, el ecosistema se compone de los siguientes elementos:

* **API Gateway**: Punto único de entrada que gestiona el enrutamiento, la seguridad y la comunicación centralizada hacia los microservicios.
* **Backend For Frontend (BFF)**: Capa de orquestación dedicada a gestionar la interacción entre el cliente y los servicios internos, optimizando la entrega de datos.
* **Servicio de Listas de Espera (Módulo 1)**: Microservicio independiente responsable del registro y administración de pacientes en espera de atención.
* **Servicio de Reasignación Automática (Módulo 2)**: Módulo lógico encargado de optimizar el uso de horas médicas mediante algoritmos de reasignación ante cancelaciones.

---

## 3. Implementación de Patrones de Diseño y Resiliencia
La robustez del backend se garantiza mediante la aplicación de patrones industriales que aseguran la modularidad y el rendimiento del sistema:

* **Repository Pattern**: Utilizado para la persistencia de datos mediante **JPA y entidades**, desacoplando la lógica de negocio del acceso directo a la base de datos.
* **Factory Method**: Implementado para la creación dinámica de instancias, facilitando la extensión de funcionalidades sin afectar el núcleo del sistema.
* **Circuit Breaker**: Mecanismo de resiliencia configurado para manejar fallos en la comunicación entre microservicios, previniendo caídas en cascada y asegurando la disponibilidad del servicio.

---

## 4. Gestión de Datos y Persistencia
El sistema implementa una estrategia de persistencia aislada para garantizar la autonomía de los servicios:
* **Aislamiento de Datos**: Cada microservicio de dominio posee su propia base de datos **PostgreSQL**, gestionada mediante JPA.
* **Optimización**: Uso de entidades JPA y procedimientos almacenados (SPs) para la ejecución de operaciones complejas con altos volúmenes de datos.

---

## 5. Estándares de Calidad y Ciclo de Vida
El desarrollo sigue rigurosos protocolos de ingeniería para asegurar una solución robusta y confiable antes de su despliegue.

### Control de Versiones (Git Flow)
Se utiliza una estrategia de branching profesional (**Git Flow**) para coordinar el desarrollo colaborativo, asegurando un historial de versiones limpio y trazable.

### Aseguramiento de la Calidad (QA)
De acuerdo con las exigencias de la **Sección 3** del caso semestral:
* **Pruebas Unitarias**: Implementación de pruebas con una cobertura mínima del **60% del código** en todos los componentes.
* **SonarQube**: Validación sistemática de cobertura, deuda técnica y cumplimiento de estándares de codificación.
* **Integración Continua (CI)**: Pipeline automatizado que garantiza la ejecución de pruebas unitarias en cada actualización del repositorio.

---

## 6. Infraestructura y Despliegue
El sistema está diseñado para operar en entornos de contenedores, facilitando la escalabilidad y sostenibilidad de la infraestructura.

### Stack Tecnológico
* **Lenguaje**: Java 17+.
* **Framework**: Spring Boot 3.x / Spring Cloud.
* **Persistencia**: Spring Data JPA + PostgreSQL.
* **Orquestación**: Docker y Docker Compose.

---

## 7. Ética, Sostenibilidad y Responsabilidad
El diseño de la plataforma RedNorte no solo responde a una necesidad técnica, sino que se alinea con principios fundamentales de responsabilidad profesional:

* **Privacidad de Datos**: Se garantiza el manejo ético de información sensible mediante el aislamiento de datos por microservicio, asegurando que la información clínica sea procesada solo por los servicios autorizados.
* **Seguridad y Confidencialidad**: La implementación de un API Gateway permite centralizar la seguridad y validar el acceso antes de interactuar con la lógica de negocio.
* **Sostenibilidad Técnica**: La arquitectura de microservicios asegura la escalabilidad del sistema a largo plazo, permitiendo que la plataforma crezca según la demanda hospitalaria sin requerir una reestructuración completa.

---

## 8. Interfaz de Integración (API REST & BFF)
El backend actúa como un proveedor de servicios robusto diseñado para la interoperabilidad:

* **Backend For Frontend (BFF)**: Este componente orquesta las peticiones del cliente, transformando y combinando datos de múltiples microservicios para optimizar el rendimiento de la red.
* **Contrato de API REST**: Se exponen endpoints estandarizados que facilitan el desacoplamiento total entre la lógica del servidor y cualquier interfaz externa.

---

## 9. Análisis de Decisiones y Reflexión Crítica
Como parte del proceso de evaluación final (EFT), se analizan las decisiones técnicas que sustentan la solución:

### Efectividad de Patrones
* **Circuit Breaker**: Es fundamental para la estabilidad del sistema, ya que previene fallos en cascada si un microservicio de salud se encuentra saturado.
* **Repository Pattern**: Ha contribuido significativamente a la modularidad y reutilización de código, permitiendo una gestión de persistencia limpia mediante JPA.

### Desafíos de Integración
* La integración de componentes backend y bases de datos independientes representó el mayor desafío técnico, resuelto mediante una configuración estricta en el ecosistema de microservicios para asegurar la cohesión del sistema.
* Se identifica como mejora futura la transición hacia una arquitectura orientada a eventos para desacoplar aún más la comunicación entre el registro de pacientes y la reasignación automática.

---

## 10. Aseguramiento de Calidad y Pruebas
La confiabilidad del sistema está respaldada por una estrategia de validación rigurosa:

* **Cobertura de Código**: Se ha alcanzado una cobertura mínima del 60% mediante pruebas unitarias implementadas en todos los componentes del sistema.
* **Validación Continua**: El uso de SonarQube permite identificar de forma proactiva vulnerabilidades y deudas técnicas, asegurando la mantenibilidad del software.

---

## 11. Endpoints Principales y Contrato de API
Para garantizar el desacoplamiento y la eficiencia, el sistema expone los siguientes puntos de acceso gestionados por el API Gateway:

| Microservicio | Endpoint Base | Responsabilidad |
| :--- | :--- | :--- |
| **Waiting List Service** | `/api/v1/patients` | Gestión CRUD de pacientes en espera. |
| **Reassignment Service** | `/api/v1/optimize` | Ejecución de lógica de reasignación automática. |
| **Portal Salud (BFF)** | `/api/v1/portal` | Orquestación de datos para el paciente. |

---

## 12. Conclusiones y Reflexión Retrospectiva
Este proyecto consolida la capacidad de diseñar soluciones escalables bajo presión y estándares de salud pública. La aplicación de patrones como Circuit Breaker no solo es técnica, sino ética, ya que garantiza la continuidad de un servicio crítico de salud. Se reconoce que la transición a microservicios independientes fue el mayor aprendizaje en términos de cohesión y mantenibilidad del software.

---

## 13. Guía de Ejecución Local

1. **Compilación de módulos**:
   ```bash
   mvn clean package -DskipTests
   ```
   # 2. Orquestación con Docker Compose
   docker-compose up -d --build

   # 3. Verificación de Salud de los Servicios
   # Eureka Dashboard: http://localhost:8761
   # API Gateway: http://localhost:8080
   # SonarQube (Calidad): http://localhost:9000
