# Stage 1: Build with JDK 17 and Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with JDK 17 (same as build stage)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
