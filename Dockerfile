#Dockerfile

FROM openjdk:8-jre-alpine
MAINTAINER Serhii Mazur <serhiy.mazur0@gmail.com>

COPY target/elasticsearch-2.0.4.RELEASE.jar /app.jar

CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]
