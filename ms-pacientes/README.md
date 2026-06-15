# Microservicio de Pacientes

## Descripción
Gestión del registro, consulta y actualización de la información demográfica y clínica de los pacientes.

## Funcionalidades
- CRUD completo de pacientes.
- **Caché Distribuida**: Integración con Redis para optimizar la latencia en lecturas frecuentes.
- **Consumidor de Eventos**: Escucha eventos provenientes de RabbitMQ (`citaAsignada`) para ejecutar acciones asíncronas.

## Configuración
Requiere conexión a PostgreSQL y un servidor Redis activo.