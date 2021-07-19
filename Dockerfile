FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /deployments
RUN mkdir /deployments/plugins
COPY jems-server/build/libs/jems-server.jar /deployments/application.jar
COPY jems-server/build/libs/jems-standard-plugin*.jar /deployments/plugins/
RUN chmod -R 700 /deployments
WORKDIR "/deployments"
CMD ["java", "-Dloader.path=plugins", "-jar", "application.jar"]
