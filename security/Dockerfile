# Usamos una imagen de Java 17 ligera
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo .jar generado por Maven al contenedor
COPY target/*.jar app.jar

# Exponemos el puerto (opcional, pero buena práctica para documentar)
EXPOSE 8083

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]