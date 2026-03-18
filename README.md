# RedNorte Healthcare Platform: Backend Core System

## 1. Introducción y Contexto Institucional
El **Servicio Público de Salud RedNorte** administra una red de establecimientos asistenciales que enfrentan desafíos críticos en la gestión de listas de espera para consultas médicas, procedimientos y cirugías. La falta de integración entre los sistemas actuales genera ineficiencias operativas, pérdida de horas médicas por cancelaciones de último momento y una escasa visibilidad de las solicitudes para los pacientes.

Esta plataforma representa una solución tecnológica centralizada basada en una **arquitectura de microservicios escalable**, diseñada para optimizar la asignación de citas, garantizar la transparencia y asegurar el manejo ético de datos clínicos.

---

## 2. Ecosistema de Microservicios y Seguridad
El sistema se fundamenta en un diseño modular y desacoplado, aplicando el principio de **Responsabilidad Única (SRP)**. Se ha dividido la lógica de negocio en servicios independientes orquestados bajo el ecosistema de **Spring Cloud**.


### Infraestructura y Seguridad (Core)
* **API Gateway**: Punto único de entrada que gestiona el enrutamiento y centraliza la seguridad perimetral.
* **Auth Service (Identity & Login)**: Microservicio dedicado a la autenticación. Implementa seguridad basada en **JWT (JSON Web Tokens)** y **RBAC (Role-Based Access Control)** para proteger la privacidad del paciente.
* **Backend For Frontend (BFF)**: Capa de orquestación dedicada a gestionar la interacción y entrega de datos hacia las interfaces de usuario.

### Módulos de Negocio (Dominios)
**Módulo 1: Sistema Integrado de Listas de Espera**
* 🏥 **Patient Service**: Única fuente de la verdad sobre la identidad del paciente (datos demográficos y previsión).
* 📋 **Waiting List Service**: Gestiona puramente la "fila", registrando ingresos, especialidad, nivel de prioridad (Triage) y estados.

**Módulo 2: Sistema de Reasignación Automática**
* 📅 **Agenda Service**: Administra los bloques de tiempo médico, boxes y registra las cancelaciones de último minuto.
* ⚙️ **Reassignment Service**: Motor lógico central. Cruza los cupos liberados por la agenda con la lista de espera para realizar la asignación prioritaria.

**Módulo 3: Portal de Información para Pacientes**
* 🔔 **Portal & Notification Service**: Puente de comunicación. Permite al paciente consultar su posición en la lista y envía alertas automáticas garantizando la transparencia.

---

## 3. Implementación de Patrones de Diseño Senior
Para asegurar que la solución sea robusta, extensible y resiliente ante fallos, se implementaron tres patrones que blindan el ciclo de vida del software:

* **Repository Pattern (Independencia de Datos)**: Utilizado en todos los servicios para aislar la lógica de negocio de la base de datos (PostgreSQL). Permite futuras migraciones tecnológicas sin alterar el núcleo del sistema.
* **Factory Method (Flexibilidad Creacional)**: Implementado en el motor de reasignación para construir dinámicamente diferentes tipos de "Asignaciones" (Urgencias, Consultas, Cirugías) según el perfil clínico, evitando la complejidad de reglas anidadas.
* **Circuit Breaker (Supervivencia y Resiliencia)**: Mecanismo configurado para manejar fallos de red. Si el servicio de Agenda cae, el circuito se abre para proteger al resto de la plataforma, permitiendo que el Portal del Paciente siga operando con respuestas de contingencia (*Fallbacks*).


---

## 4. Estrategia de Seguridad: Roles y Privacidad
Dada la sensibilidad de los historiales médicos, el sistema implementa un modelo de autorización estricto:
* **Paciente**: Acceso restringido únicamente a su estatus personal y notificaciones.
* **Administrativo**: Permisos para gestionar el ingreso de pacientes a la lista de espera.
* **Sistema (Interno)**: El motor de reasignación posee permisos de máquina-a-máquina para cruzar datos sin intervención humana.

---

## 5. Gestión de Datos y Persistencia
* **Aislamiento de Datos (Database per Service)**: Cada microservicio de dominio posee su propia base de datos **PostgreSQL**, gestionada mediante JPA, evitando el acoplamiento de datos.
* **Optimización**: Uso de entidades JPA y procedimientos almacenados (SPs) para la ejecución eficiente de operaciones con altos volúmenes de registros.

---

## 6. Estándares de Calidad y Ciclo de Vida (QA)
El desarrollo sigue rigurosos protocolos de ingeniería de software:
* **Control de Versiones**: Estrategia de branching **Git Flow** para coordinar el desarrollo colaborativo y mantener un historial limpio.
* **Pruebas Unitarias**: Cobertura mínima del **60% del código** en todos los componentes críticos.
* **SonarQube**: Validación sistemática de cobertura, deuda técnica y cumplimiento de estándares de codificación.
* **Integración Continua (CI)**: Pipeline automatizado que garantiza la ejecución de pruebas en cada actualización.

---

## 7. Infraestructura y Despliegue
El sistema está diseñado para operar nativamente en la nube mediante contenedores, garantizando escalabilidad y sostenibilidad.
* **Lenguaje**: Java 17+.
* **Framework**: Spring Boot 3.x / Spring Cloud.
* **Persistencia**: Spring Data JPA + PostgreSQL.
* **Orquestación**: Docker y Docker Compose.

---

## 8. Ética, Sostenibilidad y Responsabilidad
* **Privacidad de Datos**: El uso de JWT y la separación por microservicios asegura que la información clínica sea procesada solo por los servicios autorizados.
* **Sostenibilidad Técnica**: La arquitectura distribuida permite que la plataforma escale horizontalmente según la demanda del hospital, optimizando el uso de servidores.

---

## 9. Análisis de Decisiones y Reflexión Crítica
Como parte del proceso de evaluación final (EFT), se concluye que:
* La integración de bases de datos independientes representó el mayor desafío, resuelto mediante la correcta configuración de Spring Cloud.
* Se identifica como mejora futura la transición hacia una **Arquitectura Orientada a Eventos (EDA)** mediante un bus de mensajes (ej. Kafka/RabbitMQ) para desacoplar de forma asíncrona la comunicación entre la cancelación de horas y la reasignación automática.

---

## 10. Endpoints Principales y Contrato de API
El sistema expone puntos de acceso centralizados gestionados por el API Gateway:

| Microservicio | Endpoint Base | Responsabilidad |
| :--- | :--- | :--- |
| **Auth Service** | `/api/v1/auth` | Generación y validación de tokens JWT. |
| **Waiting List Service** | `/api/v1/waiting-list` | Gestión CRUD de pacientes en espera. |
| **Reassignment Service**| `/api/v1/reassignment` | Ejecución del algoritmo de optimización médica. |
| **Agenda Service** | `/api/v1/agenda` | Gestión de bloques de tiempo médico. |
| **Portal Salud (BFF)** | `/api/v1/portal` | Orquestación de datos de transparencia para el paciente. |

---

## 11. Conclusiones y Reflexión Retrospectiva
Este proyecto consolida la capacidad de diseñar soluciones escalables bajo presión y estándares de salud pública. La aplicación de patrones como Circuit Breaker no solo es técnica, sino ética, ya que garantiza la continuidad de un servicio crítico de salud. Se reconoce que la transición a microservicios independientes fue el mayor aprendizaje en términos de cohesión y mantenibilidad del software.

---

## 12. Guía de Ejecución Local

1. **Compilación de Artefactos (Maven)**:
   Genera los ejecutables `.jar` de cada microservicio en el ecosistema, limpiando construcciones previas y omitiendo la fase de pruebas para agilizar el despliegue local.
   ```bash
   mvn clean package -DskipTests
2. **Orquestación de Infraestructura (Docker Compose)**:
   Levanta todo el ecosistema de contenedores en segundo plano (`-d`), forzando la construcción de las imágenes más recientes (`--build`). Este paso inicializará automáticamente:
   * Las bases de datos aisladas (PostgreSQL).
   * El Service Discovery (Eureka Server).
   * El enrutador central (API Gateway).
   * Los microservicios de dominio (Listas de Espera y Reasignación).
   
   ```bash
   docker-compose up -d --build
3. **Verificación de Salud de los Servicios**:
   Una vez que los contenedores estén en ejecución, puedes monitorear el estado del ecosistema en los siguientes enlaces:

   | Servicio | Puerto | URL de Acceso | Descripción |
   | :--- | :---: | :--- | :--- |
   | **Eureka Server** | `8761` | [http://localhost:8761](http://localhost:8761) | Panel de descubrimiento de microservicios. |
   | **API Gateway** | `8080` | [http://localhost:8080](http://localhost:8080) | Punto de entrada unificado y ruteo. |
   | **SonarQube** | `9000` | [http://localhost:9000](http://localhost:9000) | Panel de calidad y cobertura de código. |
