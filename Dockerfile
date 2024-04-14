#only java to run our jar
FROM openjdk:21

WORKDIR /app
COPY target/javabot-0.0.1.jar /app/
COPY wallet ./wallet

ENV DIRECTORY=/app/wallet

EXPOSE 8080
ENTRYPOINT ["java","-jar","javabot-0.0.1.jar"]
