FROM eclipse-temurin:25-jdk-alpine

WORKDIR /site
EXPOSE  8000
RUN apt-get update && \
    rm -rf /var/lib/apt/lists/* 


CMD [ "jwbserver", "-b", "0.0.0.0" ]