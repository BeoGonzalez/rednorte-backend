# Microservicio de Seguridad (ms-security)

## Descripción
Servicio crítico encargado de la gestión de identidades, autenticación y autorización.

## Funcionalidades
- Gestión de usuarios mediante `UsuarioRepository`.
- Emisión y validación de tokens JWT.
- Configuración de Spring Security con encriptación de contraseñas (BCrypt).
- Implementación de `CustomUserDetailsService` para la integración con Spring Security.

## Endpoints Principales
- `POST /api/auth/register`: Registro de nuevos usuarios.
- `POST /api/auth/login`: Autenticación y emisión de JWT.
- `GET /api/auth/validate`: Validación de tokens para otros microservicios.