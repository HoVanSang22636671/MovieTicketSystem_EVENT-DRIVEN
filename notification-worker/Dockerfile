FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -DskipTests -pl notification-worker -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/notification-worker/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
