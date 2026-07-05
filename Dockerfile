FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE_NAME
WORKDIR /app
COPY . .
RUN mvn -pl ${SERVICE_NAME} -am clean package -DskipTests

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
