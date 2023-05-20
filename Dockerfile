FROM maven:3.8.3-openjdk-17
WORKDIR .
EXPOSE 8080
RUN mvn package
ARG JAR_FILE=target/schedule-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
