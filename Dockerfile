# Use the base image as JDK 11
FROM openjdk:17

# Create an Directory App
RUN mkdir /app

# Copy contents of target directory into app
COPY target/ /app

COPY src/main/resources/application.properties /app/application.properties

# Set the Working Directory as app
WORKDIR /app



# Execute the jar file which will run the project on port 9090
CMD java -jar pet-clinic-1.0.0.jar --spring.config.location=application.properties
