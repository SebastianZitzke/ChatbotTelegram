# --- ETAPA 1: Construcción (Build) ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos los archivos de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# --- ESTA ES LA LÍNEA QUE ARREGLA EL PROBLEMA ---
# Damos permisos de ejecución al script de Maven
RUN chmod +x ./mvnw

# Ahora sí descargamos las dependencias
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos la aplicación
RUN ./mvnw clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el .jar compilado desde la etapa anterior
COPY --from=build /app/target/botTurnos-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]