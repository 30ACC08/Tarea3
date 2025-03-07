FROM eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ src/
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 5173
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]