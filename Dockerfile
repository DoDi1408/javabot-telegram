#only java to run our jar
FROM openjdk:21

WORKDIR /app
COPY target/javabot-0.0.1.jar /app/
COPY /home/jenkins/wallet ./wallet

ENV DATABASE_URL=jdbc:oracle:thin:@bdautonoma1_medium?TNS_ADMIN=/app/wallet
ENV DIRECTORY=/app/wallet

EXPOSE 8080
ENTRYPOINT ["java","-jar","javabot-0.0.1.jar"]
