# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-slim

WORKDIR /pipeline_lab

# Copy the compiled Java .jar file into the container
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
