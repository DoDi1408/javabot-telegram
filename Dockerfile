#use maven image as build
FROM maven AS build
#where we will store the app within the container
WORKDIR /app
#Sort of replicating our file structure within the app
COPY pom.xml .
COPY src ./src
COPY wallet ./wallet
#BUILD, first install, then test, then create exec(jar)
RUN mvn verify
RUN echo 'build with maven stage completed'


#we use multistages as we do not need maven in the end, only java to run our jar
FROM openjdk:21

WORKDIR /app
#ARG JAR_FILE=target/leccion-14-1.0.0-SNAPSHOT.jar

COPY --from=build /app/target/leccion-14-1.0.0-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","leccion-14-1.0.0-SNAPSHOT.jar"]
