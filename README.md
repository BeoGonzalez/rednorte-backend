# RedNorte Healthcare Platform: Backend Core System

## 1. Introducción y Contexto Institucional
[cite_start]El Servicio Público de Salud RedNorte administra una red de hospitales y centros de atención primaria que enfrentan desafíos críticos en la gestión de listas de espera para consultas y procedimientos[cite: 106, 113]. [cite_start]La falta de integración entre los sistemas actuales genera ineficiencias, como la pérdida de horas médicas por cancelaciones y una escasa visibilidad de las solicitudes para los pacientes[cite: 114, 116, 118].

[cite_start]Esta plataforma representa una solución tecnológica centralizada basada en microservicios, diseñada para optimizar la asignación de citas y mejorar la transparencia en la comunicación institucional[cite: 107, 120].

---

## 2. Arquitectura de Microservicios
[cite_start]El sistema se basa en una arquitectura de microservicios escalable y desacoplada, permitiendo que cada componente funcione de manera modular para facilitar futuras mejoras sin comprometer la estabilidad global[cite: 126, 131].



### Componentes del Ecosistema
[cite_start]Siguiendo los requerimientos técnicos del proyecto, el backend se compone de elementos construidos mediante arquetipos Maven personalizados[cite: 143]:

* [cite_start]**API Gateway:** Punto único de entrada que gestiona la comunicación entre el frontend y el ecosistema de microservicios[cite: 129].
* [cite_start]**Backend For Frontend (BFF):** Componente dedicado a orquestar la interacción específica entre la interfaz de usuario y los servicios internos[cite: 144].
* [cite_start]**Servicio de Listas de Espera:** Microservicio independiente responsable del registro y administración de pacientes en espera de atención[cite: 122, 145].
* [cite_start]**Servicio de Reasignación Automática:** Módulo lógico encargado de optimizar el uso de horas médicas ante cancelaciones[cite: 123, 145].

---

## 3. Implementación de Patrones de Diseño
[cite_start]La robustez y mantenibilidad del sistema se fundamentan en la aplicación de patrones arquitectónicos y de diseño exigidos por la institución[cite: 130, 149]:

* [cite_start]**Repository Pattern:** Implementado para la persistencia de datos mediante JPA y entidades, asegurando el desacoplamiento entre la lógica de negocio y el acceso a datos[cite: 130, 146].
* [cite_start]**Factory Method:** Utilizado para la creación dinámica de instancias, promoviendo la modularización del sistema[cite: 130].
* [cite_start]**Circuit Breaker:** Mecanismo de resiliencia configurado para manejar fallos en la comunicación entre servicios y prevenir errores en cascada[cite: 130].



---

## 4. Estándares de Calidad y Ciclo de Vida
[cite_start]El desarrollo de la plataforma RedNorte sigue rigurosos estándares de ingeniería para garantizar una solución robusta y confiable[cite: 153, 154].

### Gestión de Versiones (Git Flow)
[cite_start]Se utiliza una estrategia de branching profesional (Git Flow o GitHub Flow) para coordinar el desarrollo colaborativo y la integración eficiente de cambios[cite: 148, 151].



### Aseguramiento de la Calidad (QA)
[cite_start]De acuerdo con las exigencias de la etapa final del proyecto, el sistema cumple con[cite: 156, 157]:
* [cite_start]**Pruebas Unitarias:** Implementación de pruebas con una cobertura mínima del 60% del código[cite: 158].
* [cite_start]**SonarQube:** Validación sistemática de estándares de calidad y cobertura de código[cite: 159].
* [cite_start]**Integración Continua (CI):** Pipeline automatizado para la ejecución de pruebas unitarias en cada actualización del repositorio[cite: 161].



---

## 5. Infraestructura y Despliegue
[cite_start]El sistema utiliza contenedores para garantizar la paridad entre los entornos de desarrollo y producción, asegurando la escalabilidad del sistema[cite: 155].

### Stack Tecnológico
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x
* [cite_start]**Persistencia:** Spring Data JPA + PostgreSQL (Bases de datos independientes) [cite: 145, 146]
* **Contenedores:** Docker y Docker Compose

### Guía de Ejecución Local
1. **Construcción de Artefactos:**
   ```bash
   mvn clean package -DskipTests
