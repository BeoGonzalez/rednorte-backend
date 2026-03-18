# RedNorte Healthcare Platform: Backend Core System

## 1. Introducción y Contexto Institucional
El Servicio Público de Salud RedNorte administra una red de establecimientos asistenciales que enfrentan desafíos críticos en la gestión de listas de espera[cite: 106, 112]. La falta de integración entre sistemas actuales genera ineficiencias, pérdida de horas médicas y escasa visibilidad para los pacientes sobre sus solicitudes[cite: 114, 116, 118].

Esta plataforma representa una solución tecnológica centralizada basada en microservicios, diseñada para optimizar la asignación de citas, reducir cancelaciones y mejorar la transparencia institucional[cite: 107, 120].

---

## 2. Arquitectura de Microservicios
El sistema se basa en una arquitectura de microservicios escalable y desacoplada, permitiendo que cada componente funcione de manera modular para facilitar futuras mejoras sin comprometer la estabilidad global[cite: 126, 131].



### Componentes del Ecosistema
De acuerdo con los requerimientos técnicos del proyecto, el backend se compone de elementos construidos mediante arquetipos Maven personalizados[cite: 143]:

* **API Gateway:** Punto único de entrada que gestiona la comunicación entre el frontend y el ecosistema de microservicios[cite: 129].
* **Backend For Frontend (BFF):** Componente dedicado a orquestar la interacción específica entre la interfaz de usuario y los servicios internos[cite: 144].
* **Servicio de Listas de Espera:** Microservicio independiente responsable del registro y administración de pacientes en espera de atención[cite: 122, 145].
* **Servicio de Reasignación Automática:** Módulo lógico encargado de optimizar el uso de horas médicas ante cancelaciones[cite: 123, 145].

---

## 3. Implementación de Patrones de Diseño
La robustez y mantenibilidad del sistema se fundamentan en la aplicación de patrones arquitectónicos y de diseño exigidos[cite: 126, 149]:

* **Repository Pattern:** Implementado para la persistencia de datos mediante JPA y entidades, asegurando el desacoplamiento entre la lógica de negocio y el acceso a datos[cite: 130, 146].
* **Factory Method:** Utilizado para la creación dinámica de instancias, promoviendo la modularización del sistema[cite: 130].
* **Circuit Breaker:** Mecanismo de resiliencia configurado para manejar fallos en la comunicación entre servicios y prevenir errores en cascada[cite: 130].



---

## 4. Estándares de Calidad y Ciclo de Vida
El desarrollo de la plataforma RedNorte sigue rigurosos estándares de ingeniería para garantizar una solución robusta y confiable[cite: 153, 154].

### Gestión de Versiones (Git Flow)
Se utiliza una estrategia de branching profesional (Git Flow o GitHub Flow) para coordinar el desarrollo colaborativo y la integración eficiente de cambios[cite: 148, 150].



### Aseguramiento de la Calidad (QA)
De acuerdo con las exigencias de la etapa final del proyecto, el sistema cumple con:
* **Pruebas Unitarias:** Implementación de pruebas con una cobertura mínima del 60% del código[cite: 158].
* **SonarQube:** Validación sistemática de estándares de calidad y cobertura de código[cite: 159].
* **Integración Continua (CI):** Pipeline automatizado para la ejecución de pruebas unitarias en cada actualización del repositorio[cite: 161].

---

## 5. Infraestructura y Despliegue
El sistema utiliza contenedores para garantizar la paridad entre los entornos de desarrollo y producción, asegurando la escalabilidad del sistema[cite: 155, 156].

### Stack Tecnológico
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x [cite: 143]
* **Frontend:** Framework moderno (Angular/React/Vue.js) integrado vía API REST[cite: 141].
* **Persistencia:** Spring Data JPA + PostgreSQL con bases de datos independientes[cite: 145, 146].
* **Contenedores:** Docker y Docker Compose[cite: 155].

### Guía de Ejecución Local
Para levantar el ecosistema completo de microservicios y sus bases de datos, siga estos pasos:

1. **Construcción de Artefactos (desde la raíz del proyecto):**
   ```bash
   mvn clean package -DskipTests
