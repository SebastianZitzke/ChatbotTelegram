# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen oficial de Maven con Java 21 (Temurin) para compilar nuestro código.
# La nombramos "build" para referirnos a ella después.
FROM maven:3.9-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos solo los archivos necesarios para descargar las dependencias
# Esto aprovecha el cache de Docker: si el pom.xml no cambia, no vuelve a descargar todo.
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos la aplicación y creamos el .jar
# Usamos -DskipTests para que el deploy sea más rápido
RUN ./mvnw clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Empezamos desde una imagen limpia solo con el Java Runtime (JRE).
# Es mucho más pequeña y segura que la imagen de Maven (que tenía el JDK completo).
FROM eclipse-temurin:21-jre

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos ÚNICAMENTE el .jar que compilamos en la Etapa 1
# Fíjate cómo usamos --from=build para copiar desde la etapa anterior.
# Asegúrate que el nombre 'botTurnos-0.0.1-SNAPSHOT.jar' coincide con tu pom.xml
COPY --from=build /app/target/botTurnos-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8080 (el que usa Spring Boot por defecto)
EXPOSE 8080

# Este es el comando que se ejecutará cuando el contenedor arranque
# Le pasamos las variables de entorno de Render (como $TELEGRAM_BOT_TOKEN) a Java
ENTRYPOINT ["java", "-jar", "/app/app.jar"]