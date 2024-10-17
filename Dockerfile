# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests=true

# Run Stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
RUN mkdir var/lib/data
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]