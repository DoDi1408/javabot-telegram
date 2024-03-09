#use maven image as build
FROM maven AS build
#where we will store the app within the container
WORKDIR /app
#Sort of replicating our file structure within the app
COPY pom.xml .
COPY src ./src
COPY wallet ./wallet

ENV DATABASE_URL=jdbc:oracle:thin:@bdautonoma1_medium?TNS_ADMIN=/app/wallet
ENV DIRECTORY=/app/wallet
#BUILD, first install, then test, then create exec(jar)
RUN mvn verify


#we use multistages as we do not need maven in the end, only java to run our jar
FROM openjdk:21

WORKDIR /app
#ARG JAR_FILE=target/leccion-14-1.0.0-SNAPSHOT.jar

COPY --from=build /app/target/leccion-14-1.0.0-SNAPSHOT.jar .
COPY wallet ./wallet

ENV DATABASE_URL=jdbc:oracle:thin:@bdautonoma1_medium?TNS_ADMIN=/app/wallet
ENV DIRECTORY=/app/wallet

EXPOSE 8080
ENTRYPOINT ["java","-jar","leccion-14-1.0.0-SNAPSHOT.jar"]
