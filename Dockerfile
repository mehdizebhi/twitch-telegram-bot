# Build Stage
FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests=true

# Run Stage
FROM eclipse-temurin:25-jdk
WORKDIR /app
RUN mkdir -p var/lib/data
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]