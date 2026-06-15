# Microservicio de Doctores

## Descripción
Gestión de perfiles, especialidades y registros profesionales de los médicos del sistema RedNorte.

## Funcionalidades
- Gestión de perfiles médicos.
- Búsqueda avanzada por especialidad.
- Estrategia de caché con `@CachePut` y `@CacheEvict` para asegurar consistencia entre Redis y PostgreSQL tras actualizaciones.