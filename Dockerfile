# Build stage
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

# Først kun pom.xml for bedre dependency-cache
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Så resten af koden
COPY src ./src

# Byg jar uden at køre tests (tests kører vi i CI)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
