FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -DskipTests -pl movie-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/movie-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
