FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="" \
    PORT=8080

ADD entrypoint.sh entrypoint.sh

ENTRYPOINT ["sh", "/entrypoint.sh"]

EXPOSE 8080

ADD *.war app.war

