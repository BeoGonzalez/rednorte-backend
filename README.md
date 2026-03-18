# RedNorte Healthcare Platform: Backend Core System

## 1. Introducción y Contexto Institucional
El Servicio Público de Salud RedNorte administra una red de hospitales y centros de atención primaria que enfrentan desafíos críticos en la gestión de listas de espera para consultas y procedimientos. La falta de integración entre los sistemas actuales genera ineficiencias, como la pérdida de horas médicas por cancelaciones y una escasa visibilidad de las solicitudes para los pacientes.

Esta plataforma representa una solución tecnológica centralizada basada en microservicios, diseñada para optimizar la asignación de citas y mejorar la transparencia en la comunicación institucional.

---

## 2. Arquitectura de Microservicios
El sistema se basa en una arquitectura de microservicios escalable y desacoplada, permitiendo que cada componente funcione de manera modular para facilitar futuras mejoras sin comprometer la estabilidad global.



### Componentes del Ecosistema
Siguiendo los requerimientos técnicos del proyecto, el backend se compone de elementos construidos mediante arquetipos Maven personalizados:

* **API Gateway:** Punto único de entrada que gestiona la comunicación entre el frontend y el ecosistema de microservicios.
* **Backend For Frontend (BFF):** Componente dedicado a orquestar la interacción específica entre la interfaz de usuario y los servicios internos.
* **Servicio de Listas de Espera:** Microservicio independiente responsable del registro y administración de pacientes en espera de atención.
* **Servicio de Reasignación Automática:** Módulo lógico encargado de optimizar el uso de horas médicas ante cancelaciones.

---

## 3. Implementación de Patrones de Diseño
La robustez y mantenibilidad del sistema se fundamentan en la aplicación de patrones arquitectónicos y de diseño exigidos por la institución:

* **Repository Pattern:** Implementado para la persistencia de datos mediante JPA y entidades, asegurando el desacoplamiento entre la lógica de negocio y el acceso a datos.
* **Factory Method:** Utilizado para la creación dinámica de instancias, promoviendo la modularización del sistema.
* **Circuit Breaker:** Mecanismo de resiliencia configurado para manejar fallos en la comunicación entre servicios y prevenir errores en cascada.



---

## 4. Estándares de Calidad y Ciclo de Vida
El desarrollo de la plataforma RedNorte sigue rigurosos estándares de ingeniería para garantizar una solución robusta y confiable.

### Gestión de Versiones (Git Flow)
Se utiliza una estrategia de branching profesional (Git Flow o GitHub Flow) para coordinar el desarrollo colaborativo y la integración eficiente de cambios.



### Aseguramiento de la Calidad (QA)
De acuerdo con las exigencias de la etapa final del proyecto, el sistema cumple con:
* **Pruebas Unitarias:** Implementación de pruebas con una cobertura mínima del 60% del código.
* **SonarQube:** Validación sistemática de estándares de calidad y cobertura de código.
* **Integración Continua (CI):** Pipeline automatizado para la ejecución de pruebas unitarias en cada actualización del repositorio.



---

## 5. Infraestructura y Despliegue
El sistema utiliza contenedores para garantizar la paridad entre los entornos de desarrollo y producción, asegurando la escalabilidad del sistema.

### Stack Tecnológico
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x
* **Persistencia:** Spring Data JPA + PostgreSQL (Bases de datos independientes)
* **Contenedores:** Docker y Docker Compose

### Guía de Ejecución Local
1. **Construcción de Artefactos:**
   ```bash
   mvn clean package -DskipTests
