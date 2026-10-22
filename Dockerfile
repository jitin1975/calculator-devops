# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Resolve dependencies first so this layer is cached across code changes
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# curl is used by the Compose health check
RUN apk add --no-cache curl \
    && addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/calculator.jar app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
