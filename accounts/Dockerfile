FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon -x test
RUN find /app/build/libs -name "*.jar" ! -name "*plain*" -exec cp {} /app/app.jar \;

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN apk update && apk add --no-cache curl jq
COPY --from=build /app/app.jar /app/application.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "application.jar"]