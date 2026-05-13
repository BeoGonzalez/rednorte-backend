# Usamos la misma imagen base que tus otros servicios
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Copiamos el .jar generado por Maven
# Asegúrate de haber corrido 'mvn clean package' antes
COPY target/*.jar app.jar

# Exponemos el puerto definido en application.yml del BFF
EXPOSE 8090

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]