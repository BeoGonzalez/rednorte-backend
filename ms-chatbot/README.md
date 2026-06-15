# Microservicio de Chatbot (IA)

## Descripción
Servicio de triaje médico basado en inteligencia artificial.

## Arquitectura
- Integración con modelos de lenguaje mediante Spring AI.
- Procesamiento de síntomas mediante el `TriageController`.
- Manejo de excepciones especializado (`AiProviderException`).

## Requisitos
- API Key de proveedor de IA configurada en las variables de entorno.