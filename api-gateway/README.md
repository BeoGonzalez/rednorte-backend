# API Gateway

## Descripción
El API Gateway funciona como el punto de entrada unificado para el sistema RedNorte. Su propósito es centralizar el enrutamiento, la seguridad de capa perimetral y la gestión de tráfico hacia los microservicios internos.

## Funcionalidades
- Enrutamiento dinámico de peticiones hacia los microservicios mediante Spring Cloud Gateway.
- Centralización de configuraciones de red.
- Integración con Eureka Server para el descubrimiento de servicios.

## Configuración
La configuración de rutas se encuentra en `application.yml`. Asegúrese de que el puerto 8080 sea accesible desde el exterior.