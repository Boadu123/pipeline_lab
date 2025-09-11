# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-slim

# Set the working directory inside the container
WORKDIR /pipeline_lab

# Copy the compiled Java .jar file into the container at /app
# Assumes your build tool (like Maven) creates the jar in the target folder
COPY target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file when the container launches
ENTRYPOINT ["java","-jar","app.jar"]