#
# Build
#
FROM maven:3.8.4-jdk-11-slim as buildtime
WORKDIR /build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true



FROM adoptopenjdk/openjdk11:alpine-jre as builder

COPY --from=buildtime /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk/openjdk11:alpine-jre

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ADD --chown=spring:spring https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.10/applicationinsights-agent-3.4.10.jar /applicationinsights-agent.jar
COPY --chown=spring:spring docker/applicationinsights.json ./applicationinsights.json

COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./

EXPOSE 8080

COPY --chown=spring:spring docker/run.sh ./run.sh
ENTRYPOINT ["./run.sh"]
