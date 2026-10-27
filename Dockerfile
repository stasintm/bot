FROM amazoncorretto:21

WORKDIR /app

COPY target/*.jar bot.jar

ENTRYPOINT ["java", "-jar", "bot.jar"]