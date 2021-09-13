#
# Build stage
#
FROM maven:3.8.2-openjdk-16 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM adoptopenjdk/openjdk16:alpine
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
COPY wait-for-it.sh .
RUN chmod +x /wait-for-it.sh
RUN apk add --no-cache bash

ENV LD_LIBRARY_PATH=/lib

RUN wget https://download.oracle.com/otn_software/linux/instantclient/213000/instantclient-basic-linux.x64-21.3.0.0.0.zip && \
    unzip instantclient-basic-linux.x64-21.3.0.0.0.zip && \
    cp -r instantclient_21_3/* /lib && \
    rm -rf instantclient-basic-linux.x64-21.3.0.0.0.zip && \
    wget https://download.oracle.com/otn_software/linux/instantclient/213000/instantclient-sqlplus-linux.x64-21.3.0.0.0.zip && \
    unzip instantclient-sqlplus-linux.x64-21.3.0.0.0.zip && \
    cp -r instantclient_21_3/* /lib && \
    rm -rf instantclient-sqlplus-linux.x64-21.3.0.0.0.zip && \

    apk add libaio

ADD script.sh /root/script.sh

RUN chmod +x /root/script.sh
RUN /root/script.sh

EXPOSE 8080

ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
#ENTRYPOINT ["./wait-for-it.sh", "oracle-db-12c:1521", "--strict" , "--timeout=5" , "--", "java", "-jar", "/usr/local/lib/app.jar"]