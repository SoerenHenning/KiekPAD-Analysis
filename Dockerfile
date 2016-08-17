FROM maven:3-jdk-8-onbuild
MAINTAINER SoerenHenning

RUN mv -f target/kiekpad-analysis.jar kiekpad-analysis.jar

VOLUME /usr/src/app/config

CMD ["java", "-jar", "kiekpad-analysis.jar"]