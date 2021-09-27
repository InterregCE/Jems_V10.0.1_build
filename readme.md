# Joint electronic monitoring system

This application is called Jems and is build on top of a kotlin-springboot-angular template based on:

* Java / Kotlin
* Spring Boot
* Angular
* Gradle

That is backed by the Cloudflight Platform [1] to improve efficiency and quality.
Further, Jems is built by the improved Cloudflight Gradle Plugin [2] which is currently available via the Cloudflight repository.

## How to ...

### ... develop locally

Requirements:

 - Java, recommended: OpenJDK 11
 - Gradle
 - npm
 - docker or manual configuration of: MariaDB, MinIO, ElasticSearch
 - access to Cloudflight repository for the Gradle plugin

After unpacking or cloning the Jems repository, import the project in IntelliJ or a similar development environment.
For a successful build, the Cloudflight gradle plugin is needed once to be downloaded via the specified repository [3].
This can be achieved by specifying the username and token received in either the file `gradle.properties` or the local gradle configuration.
The token can be generated or received from [Interact](jems@interact-eu.net).

The following are example properties:
 - `cloudflightRepositoryUser=_cust_interact`
 - `cloudflightRepositoryPassword=TOKEN`

Synchronize or import the Gradle project.
To run the server successfully, it is needed to at least have an instance of MariaDB available.
This is configured by the property `SPRING_DATASOURCE_URL` or the default `jdbc:mariadb://localhost:3306/jems`.
To simplify the startup of Jems, a docker compose file is available (docker-compose.yml).
The following commands are used for development:

 - `gradle clean build` for building the project; specifically use `clean` if changes for generated files are necessary
 - `spring boot` with MainClass `io.cloudflight.jems.server.Application` starts server (on default port `8080`)
 - `npm run serve:local` start the angular build watch (run for automatic reload after frontend changes)
 - `PUBLISH_BUILD=true PUBLISH_NPM_AUTH=x ./gradlew assemble` should generate the full executable jems-server.jar (no snapshot)

### ... deploy to an environment

Requirements:

 - Docker to use the following easily as containers, or manually set up:
 - MariaDB (mandatory)
 - MinIO
 - Elastic Search
 - Kibana (optional, only if needed)
 - Mailhog (optional, only if you want to test sending mail notifications)

Currently, there are two environments set up for internal development [4] and testing [5] with external access.
Both are deployed on the Cloudflight OpenShift and updated automatically within each development cycle.
The build and deployment is managed within Teamcity [6].

Manual deployment using docker compose:

 - execute `gradle clean build` for building the project
 - run the following docker-compose services
   - jems-database (relational database for Jems configuration and input data)
   - jems-minio (Object storage for files)
   - audit-database (logging into elastic search, needed for Audit Logs)
   - audit-analyzer (Kibana for additional Audit Log access)
   - mailhog (mail server to test sending mail notification)
 - run the jar (jems-server `./build/libs`) as Spring Boot application
   - the webapp uses flyway to automatically migrate the relational database (mariaDB)
   - `--audit-service.url-and-port=127.0.0.1:9200` can be specified to use a local elastic search instance
   - by default Spring Management endpoints are enabled on server port + 10000, e.g. when `server.port=8080` then
   `management.server.port=18080`, so to retrieve version of build use
   [localhost:18080/actuator/info](http://localhost:18080/actuator/info)
 - use the following environment variables to control
   - `AUDIT_ENABLED=false` to enable/disable logging into elastic search
   - `MAIL_ENABLED=false` to enable/disable sending mail notifications

application.yaml can be added to root of the full executable jems-server.jar
the properties specified will override the default ones within resources/application.yaml

## Codestyle

Import `./idea/Cloudflight-codestyle.xml` to your development environment (e.g. IntelliJ) and use it.

## Additional details

### Database (Store)
This project is now based on
- [MariaDB](https://mariadb.com/kb/en/installing-and-using-mariadb-via-docker/) for storing all data
- ElasticSearch for storing "audit logs". Those are tracking specific interactions between users and the system itself.
- MinIO file storage for storing files

The easiest way to start everything needed for local development is to run it inside docker.
There is a [docker-compose](docker-compose.yml) file, so everything you need is to run `docker-compose up` command in
root directory. For IntelliJ there are also configurations
- [jems-database](.idea/runConfigurations/jems_database.xml) to start MariaDB inside Docker
- [jems-audit](.idea/runConfigurations/jems_audit.xml) to start ElasticSearch and Kibana inside Docker. Those data are
then available either directly in ElasticSearch [localhost:9200](http://localhost:9200/audit-log/audit/_search) or
in Kibana web interface [localhost:5601](http://localhost:5601)
- [jems-minio](.idea/runConfigurations/jems_minio.xml) to start MinIO file storage. MinIO is then accessible on
[localhost:9000](http://localhost:9000)

There is no need to start anything besides MariaDB, so spring-boot will also start without MinIO and without
Audit (ES). There is property value `audit-service.enabled` - if you set to `false` audits will be written
as _info \[LOG\]_ to stdout.

### API testing
For testing the API, the module [jems-rest-test](jems-rest-test) was introduced.
The generated documentation of the API can be found on the successful started Jems server [7].

### Startup parameters

You can define following startup parameters (see also [application.yaml](jems-server/src/main/resources/application.yaml)):
- `audit-service.enabled`=[true,false] (or env variable `AUDIT_ENABLED`), if this one is set to true, you need to provide also:
  - `audit-service.url-and-port` with address of ElasticSearch (or env variable `AUDIT_ELASTICSEARCH_URL_AND_PORT`)
- `spring.datasource.url` with address of MariaDB (or env variable `SPRING_DATASOURCE_URL`)
  - optional `spring.datasource.username` (by default set to `root`)
  - `spring.datasource.password` (or env variable `SPRING_DATASOURCE_PASSWORD`)
- `minio-storage.endpoint` with address of Minio (or env variable `MINIO_URL_AND_PORT`)
  - `minio-storage.accessKey` with access key for Minio (or env variable `MINIO_ACCESS_KEY`)
  - `minio-storage.secretKey` with secret key for Minio (or env variable `MINIO_SECRET_KEY`)
- `info.helpdesk-url` URL, which is available in the main HELP tooltip
- `info.accessibility-statement-url` URL for accessibility statement, which is available in the login page that can be modified by the user
- `info.terms-privacy-policy-url` URL for the Terms of service and privacy policy page, that is available in the login and register pages and can be modified by the user
- `app.translations-folder` Path for translations folder that will be uploaded by the users
- `app.notification.mail.enabled`=[true,false] (or env variable `MAIL_ENABLED`) if this one is set to true, you need to provide also:
  - `spring.mail.host` with address of SMTP server
  - `spring.mail.port` with port number on which SMTP server is listening
  - `spring.mail.username` with the username defined in the SMTP server
  - `spring.mail.password`  with the password of the specified username
  - `spring.mail.properties.mail.smtp.auth`=[true,false] (by default set to `false`)
  - optional `spring.mail.properties.mail.smtp.connectiontimeout` (by default set to `5000`)
  - optional `spring.mail.properties.mail.smtp.timeout` (by default set to `5000`)
  - optional `spring.mail.properties.mail.smtp.writetimeout` (by default set to `5000`)
  - optional `spring.mail.properties.mail.smtp.starttls.enabled` (by default set to `false`)
- `app.notification.mail.sender` The sender address that will be used as from in the outgoing mail notifications
- `app.notification.mail.bcc-list` List of BCC recipients that should receive a copy of all the outgoing mail notifications


### Plugins

if plugins are to be used, the following startup parameter should be used
- `-Dloader.path`=[plugins] for using the folder 'plugins' next to the jems-server as root to scan for jems plugins

further this section in the [application.yaml] specifies which paths are taken for their translation files.
the following parameters are set by default:
```
spring:
   messages:
     basename: classpath:/messages, classpath:/plugin_messages
```
since all translations for one language end up in the same file, plugin translation files as well as their translation keys need to use a specific unique plugin identifier!
if the main translation files need to be exchanged, instead of the `classpath:/messages` an own path should be used.

## References

1. [Cloudflight Platform](https://git.internal.cloudflight.io/cloudflight/libs/cloudflight-platform)
2. [Cloudflight Gradle Plugin](https://git.internal.cloudflight.io/cloudflight/gradle/cloudflight-gradle-plugin)
3. https://artifacts.cloudflight.io/repository/plugins-maven
4. https://jems-dev.internal.cloudflight.dev/
5. https://jems-test.cloudflight.dev/
6. [Teamcity](https://teamcity.internal.cloudflight.io/)
7. [Generated Swagger API documentation](https://jems-test.cloudflight.dev/swagger-ui.html#/)
