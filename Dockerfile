FROM openjdk:8-alpine

COPY build/libs/andriis-test-1.0.0.jar /app.jar
EXPOSE 48091
CMD ["java", "-jar","app.jar"]