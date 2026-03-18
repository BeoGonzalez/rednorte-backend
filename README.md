# RedNorte Healthcare Platform: Backend Core System

## Introducción y Contexto Institucional
[cite_start]El Servicio Público de Salud RedNorte administra una red de hospitales y centros de atención primaria que enfrentan desafíos críticos en la gestión de listas de espera para consultas y procedimientos[cite: 106]. [cite_start]La actual falta de integración entre sistemas genera ineficiencias como la pérdida de horas médicas y una escasa visibilidad para los pacientes[cite: 114, 116].

[cite_start]Esta plataforma representa una solución tecnológica centralizada basada en microservicios, diseñada para optimizar la asignación de citas y mejorar la comunicación institucional[cite: 107, 120].

---

## Arquitectura de Microservicios
[cite_start]El sistema se ha diseñado bajo un arquetipo de microservicios escalables y desacoplados, permitiendo que cada componente evolucione de forma independiente sin afectar la estabilidad global[cite: 126, 131].



### Componentes Principales
[cite_start]De acuerdo con los requerimientos técnicos de la asignatura DSY1106, el backend se compone de elementos construidos mediante arquetipos Maven personalizados[cite: 143]:

* [cite_start]**API Gateway**: Actúa como el punto de entrada único, gestionando la comunicación entre el frontend y los servicios internos[cite: 129].
* [cite_start]**Backend For Frontend (BFF)**: Gestiona la interacción específica entre la interfaz de usuario y los microservicios, optimizando el flujo de datos[cite: 144].
* [cite_start]**Servicio de Listas de Espera**: Microservicio independiente encargado del registro y administración de pacientes[cite: 122, 145].
* [cite_start]**Servicio de Reasignación Automática**: Componente lógico para la optimización de horas médicas disponibles tras cancelaciones[cite: 123, 145].

---

## Implementación de Patrones de Diseño
[cite_start]La robustez de la plataforma se garantiza mediante la aplicación de patrones arquitectónicos y de diseño específicos definidos en el encargo[cite: 130, 149]:

1. [cite_start]**Repository Pattern**: Utilizado para la persistencia de datos mediante JPA y entidades, asegurando un desacoplamiento efectivo entre la lógica de negocio y la base de datos[cite: 130, 146].
2. [cite_start]**Factory Method**: Implementado para la creación dinámica de instancias, facilitando la modularización del sistema[cite: 130].
3. [cite_start]**Circuit Breaker**: Mecanismo de resiliencia configurado para manejar fallos en la comunicación entre servicios y prevenir caídas en cascada[cite: 130].



---

## Estrategia de Desarrollo y Gestión de Calidad

### Control de Versiones
[cite_start]Se aplica una estrategia de branching profesional mediante **Git Flow** o **GitHub Flow** para coordinar el desarrollo colaborativo y la integración de cambios de manera eficiente[cite: 148, 152].

### Aseguramiento de la Calidad (QA)
[cite_start]Siguiendo los estándares de calidad del Servicio de Salud RedNorte, el proyecto implementa[cite: 154]:
* [cite_start]**Pruebas Unitarias**: Cobertura mínima del 60% del código en todos los componentes[cite: 158].
* [cite_start]**SonarQube**: Herramienta utilizada para la validación de cobertura y análisis estático de código[cite: 159].
* [cite_start]**Integración Continua (CI)**: Pipeline automatizado que ejecuta pruebas unitarias en cada actualización del repositorio[cite: 161].



---

## Infraestructura y Despliegue (Docker)
[cite_start]El sistema utiliza contenedores para garantizar la paridad entre entornos de desarrollo y producción[cite: 169].

### Requisitos Técnicos
* **Runtime**: Java 17 o superior.
* [cite_start]**Gestión de Dependencias**: Maven (Arquetipos personalizados)[cite: 143].
* [cite_start]**Contenedores**: Docker y Docker Compose[cite: 169].
* [cite_start]**Persistencia**: PostgreSQL mediante JPA y entidades[cite: 146].

### Guía de Ejecución Local
1. **Construcción de Artefactos**:
   ```bash
   mvn clean package -DskipTests



