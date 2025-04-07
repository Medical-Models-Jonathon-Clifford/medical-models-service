FROM amazoncorretto:23-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build stage
COPY ./target/*.jar ./app.jar

# Expose the port on which the application will run
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]