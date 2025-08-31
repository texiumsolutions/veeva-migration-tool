# Use Java runtime image
FROM eclipse-temurin:21-jre

# Set work directory
WORKDIR /app

# Copy only the JAR file into the image
COPY myapp-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

