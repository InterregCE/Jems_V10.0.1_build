FROM eclipse-temurin:17-jre
RUN mkdir /deployments
RUN mkdir /deployments/plugins
RUN chmod -R ugo+rwx /deployments
COPY jems-server/build/libs/jems-server.jar /deployments/application.jar
COPY jems-server/build/libs/jems-standard-plugin*.jar /deployments/plugins/
WORKDIR "/deployments"
CMD ["java", "-Dloader.path=plugins", "-jar", "application.jar"]
