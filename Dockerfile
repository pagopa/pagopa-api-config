FROM adoptopenjdk/openjdk16:alpine
ARG JAR_FILE=target/*.jar
# TODO: build mvn
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]