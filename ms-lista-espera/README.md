# Microservicio de Lista de Espera

## Descripción
Core de negocio responsable de la gestión de solicitudes de atención médica y la priorización de citas.

## Lógica de Negocio
- **Priorización**: Algoritmo interno para la ordenación de solicitudes.
- **Automatización**: `AsignadorCitasJob` (Spring Scheduled) que procesa citas pendientes cada 60 segundos.
- **Integración**: Productor de eventos en RabbitMQ para notificar la asignación de citas.

## Tecnologías
- PostgreSQL para persistencia.
- RabbitMQ para comunicación asíncrona (Event-Driven).
- Spring Cloud Stream para la gestión de mensajes.