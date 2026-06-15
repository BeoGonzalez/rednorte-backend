# Backend For Frontend (BFF)

## Descripción
Capa intermedia de agregación diseñada para optimizar las interacciones entre el frontend (Angular) y los microservicios de backend.

## Arquitectura
Implementa el patrón BFF para consolidar datos de múltiples microservicios (como `ms-doctores` y `ms-pacientes`) en un único objeto de respuesta, reduciendo la cantidad de llamadas HTTP que el cliente debe realizar.

## Componentes clave
- **Feign Clients**: Comunicación declarativa con servicios de backend.
- **Resilience**: Manejo de errores distribuido mediante FeignException.
- **Seguridad**: Protección mediante JWT validado a través de Spring Security.