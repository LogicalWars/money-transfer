FROM alpine/java:21-jdk

COPY target/money-transfer-0.0.1-dev.jar app.jar

EXPOSE 5500

CMD ["java", "-jar", "app.jar"]