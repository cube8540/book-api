FROM ubuntu:18.04

RUN apt-get update && apt-get install -y unzip wget vim

RUN wget https://download.java.net/java/GA/jdk16.0.1/7147401fd7354114ac51ef3e1328291f/9/GPL/openjdk-16.0.1_linux-x64_bin.tar.gz
RUN wget https://services.gradle.org/distributions/gradle-7.1-bin.zip

RUN tar -xvzf openjdk-16.0.1_linux-x64_bin.tar.gz -C /lib
RUN unzip gradle-7.1-bin.zip -d /lib

ENV JAVA_HOME /lib/jdk-16.0.1
ENV GRADLE_HOME /lib/gradle-7.1
ENV PATH $JAVA_HOME/bin:$PATH
ENV PATH $GRADLE_HOME/bin:$PATH

RUN mkdir /lib/book-api
RUN mkdir /var/log/api
RUN mkdir /var/log/api/book
RUN mkdir /var/log/api/book/root
RUN mkdir /var/log/api/book/error

ARG V_VERSION
ARG V_PROFILE
ARG V_API_LOG_VOLUME=/var/log/api/book

ENV API_VERSION=${V_VERSION}
ENV API_PROFILE=${V_PROFILE}

ADD /build/libs/book-api-${API_VERSION}.jar /lib/book-api/book-api.jar

VOLUME ["$V_API_LOG_VOLUME"]
ENTRYPOINT java -jar -Dspring.profiles.active=$API_PROFILE /lib/book-api/book-api.jar