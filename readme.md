# Joint electronic monitoring system

This application is called Jems and is build on top of a kotlin-springboot-angular template based on:

* Java / Kotlin
* Spring Boot
* Angular
* Gradle

That is backed by the [Cloudflight Platform]<sup>[1]</sup> to improve efficiency and quality.
Furthermore, Jems is built by the improved [Cloudflight Gradle Plugin]<sup>[2]</sup>, which is currently available via the Cloudflight repository.

## How to ...

### ... develop locally

Requirements:

 - Java, recommended: OpenJDK 17
 - Gradle
 - npm
 - docker or manual configuration of: MariaDB, MinIO, ElasticSearch
 - access to Cloudflight repository for the Gradle plugin

After unpacking or cloning the Jems repository, import the project in IntelliJ or a similar development environment (IDE).
For a successful build, the Cloudflight gradle plugin is required to be downloaded once via the [Cloudflight Artifactory]<sup>[3]</sup>.
This can be achieved by specifying the username and token received in either the `gradle.properties` file or the local gradle configuration.
The token can be generated or received from [Interact](https://www.interact-eu.net/).

The following are example properties:
 - `cloudflightRepositoryUser=_cust_interact`
 - `cloudflightRepositoryPassword=TOKEN`

Synchronize or import the Gradle project.
To run the server successfully, it is required to have at least an instance of MariaDB available.
This is configured by the property `SPRING_DATASOURCE_URL` or the default `jdbc:mariadb://localhost:3306/jemsdb`.
To simplify the startup of Jems, a docker compose file is available (docker-compose.yml).
The following commands are used for development:

 - `gradlew clean build` - for building the project; specifically use `clean` if changes for generated files are necessary
 - `spring boot` - with MainClass `io.cloudflight.jems.server.Application` starts the server (on default port `8080`)
 - `npm run serve:local` - starts the angular build watch (run for automatic reload after frontend changes)
 - `PUBLISH_BUILD=true PUBLISH_NPM_AUTH=x ./gradlew assemble` - should generate the full executable jems-server.jar (no snapshot)

### ... deploy to an environment

Requirements:

 - Docker - to use the following easily configured containers or manually set up
 - MariaDB (mandatory)
 - MinIO
 - ElasticSearch
 - Kibana (optional)
 - Mailhog (optional, only if you want to test sending mail notifications)

Currently, there are two environments set up: one for internal development ([Jems internal development]<sup>[4]</sup>) and one for testing ([Jems internal testing]<sup>[5]</sup>) with external access.
Both are deployed on Cloudflight OpenShift and updated automatically within each development cycle.
The build and deployment is managed within [TeamCity]<sup>[6]</sup>.

Manual deployment using docker compose:

 - execute `gradlew clean build` to build the project
 - run the following docker-compose services:
   - jems-database (relational database for Jems configuration and input data)
   - jems-minio (Object storage for files) [**WE DO NOT RECOMMEND THIS FOR PRODUCTION**, there you should use
     [MinIO operator](https://github.com/minio/operator)]
   - audit-database (logging into elasticsearch, needed for Audit Logs) **WE DO NOT RECOMMEND THIS FOR PRODUCTION**, there
   you should use multi-node cluster, see official guidance:
     - [ES images in Docker in production env](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-prod-prerequisites) and
     - [docker-compose.yml](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/docker.html#docker-compose-file) for production
   - audit-analyzer (Kibana for additional Audit Log access)
   - mailhog (mail server to test sending mail notifications)
 - run the jar (jems-server `./build/libs`) as Spring Boot application
   - the webapp uses flyway to automatically migrate the relational database (mariaDB)
   - `--audit-service.url-and-port=127.0.0.1:9200` can be specified to use a local elasticsearch instance
   - by default, Spring Management endpoints are enabled on server port + 10000 (e.g. when `server.port=8080`, then
   `management.server.port=18080`)
   - to use logging into file, you can specify the following parameter: `--logging.file.name="file.log"`
   - to use different log level, you can specify the following parameter example: `--logging.level.org.springframework=TRACE`
 - use the following environment variables to control:
   - `AUDIT_ENABLED=false`: enables/disables logging into elasticsearch
   - `MAIL_ENABLED=false`: enables/disables sending mail notifications
   - `SERVER_URL`: specifies the URL by which the application would be accessible publicly

`application.yaml` can be added to root of the full executable jems-server.jar.
The properties specified will override the default ones within `resources/application.yaml`

## Additional details

### For developers (how to start)
This project is based on:
- [MariaDB](https://mariadb.com/kb/en/installing-and-using-mariadb-via-docker/) - for data storage
- ElasticSearch - for storing "audit logs". Those are tracking specific interactions between users and the system itself.
- MinIO - for file storage

The easiest way to start everything needed for local development is to run it inside docker.
There is a [docker-compose](docker-compose.yml) file, so all you need to do is to run `docker-compose up` in the
root directory. For IntelliJ there are additional configurations:
- [jems-database](.idea/runConfigurations/jems_database.xml) - to start MariaDB inside Docker
- [jems-audit-elastic-search](.idea/runConfigurations/jems_audit_elastic_search.xml) to start ElasticSearch and [jems-audit-kibana](.idea/runConfigurations/jems_audit_kibana.xml) for Kibana respectively, inside Docker. Those data are
then available either directly in ElasticSearch [localhost:9200](http://localhost:9200/audit-log/audit/_search) or
in the Kibana web interface [localhost:5601](http://localhost:5601)
- [jems-minio](.idea/runConfigurations/jems_minio.xml) - to start the MinIO file storage. MinIO is then accessible on
[localhost:9000](http://localhost:9000)

There is no need to start anything besides MariaDB, as spring-boot will also start without MinIO and
Audit (ES). There is an environment variable (`AUDIT_ENABLED=true`) that if you set to `false`, the audits will be written
as _info \[LOG\]_ to stdout.

#### Logo Setup
To exchange the current default Logos used by Jems, Minio has to be used directly. There, the bucket
`jems-logo-file-bucket` has to be added. The following files are currently being used:
- InterregProgrammeLogo_200.png

#### Email Setup
To have Jems use email templates, upload the intended HTML templates to the Minio bucket: `email-templates`.
For the Mail server setup, see the 'Startup parameters' below (`app.notification.mail.enabled`, which should be true).
The following templates are currently being used:
- password-reset-link.html
- password-reset-success.html
- user-registration-confirmation.html

### API testing
For API testing, the module [jems-rest-test](jems-rest-test) was introduced.
The [Generated Swagger API documentation]<sup>[7]</sup> can be found on a successfully started Jems server.

### Startup parameters

You can define the following startup parameters (see also [application.yaml](jems-server/src/main/resources/application.yaml)):
- `audit-service.enabled`=[true,false] (**we recommend using the environment variable:** `AUDIT_ENABLED`). If this one is set to true, you also need to provide:
  - `audit-service.url-and-port` with the address of ElasticSearch (better use the environment variable: `AUDIT_ELASTICSEARCH_URL_AND_PORT`)
  - `audit-service.password` password for default 'elastic' user (better use the environment variable: `AUDIT_ELASTICSEARCH_PASSWORD`)
- `spring.datasource.url` with the address of MariaDB (or the environment variable: `SPRING_DATASOURCE_URL`)
  - [**optional**] `spring.datasource.username` (by default is set to `root`)
  - `spring.datasource.password` (or the environment variable: `SPRING_DATASOURCE_PASSWORD`)
- `minio-storage.endpoint` with the address of Minio (or the environment variable: `MINIO_URL_AND_PORT`)
  - `minio-storage.accessKey` with the access key for Minio (or the environment variable: `MINIO_ACCESS_KEY`)
  - `minio-storage.secretKey` with the secret key for Minio (or the environment variable: `MINIO_SECRET_KEY`)
- `app.captcha.enabled`=[true,false] will enable/disable captcha on the user registration endpoint (or the environment variable: `REGISTRATION_CAPTCHA_ENABLED`)
- environment variable `HELPDESK_URL` - a URL, which is available in the main HELP tooltip (will set param `info.helpdesk-url`)
- environment variable `HELPDESK_EMAIL` - an email, which is available in the main HELP section _to contact_ (will set param `info.helpdesk-email`)
- `info.accessibility-statement-url` - URL for accessibility statement, which is available in the login page that can be later modified by the user
- `info.terms-privacy-policy-url` - URL for the Terms of service and privacy policy page, that is available in the login and register pages and can be later modified by the user
- `app.translations-folder` - path to the `translations` folder that will be uploaded by the users
- `app.notification.mail.enabled`=[true,false] (or the environment variable: `MAIL_ENABLED`). If this one is set to true, you will also need to provide:
  - `spring.mail.host` with the address of SMTP server
  - `spring.mail.port` with the port number on which SMTP server is listening
  - `spring.mail.username` with the username defined in the SMTP server
  - `spring.mail.password`  with the password of the specified username
  - `spring.mail.properties.mail.smtp.auth`=[true,false] (by default is set to `false`)
  - [**optional**] `spring.mail.properties.mail.smtp.connectiontimeout` (by default is set to `5000`)
  - [**optional**] `spring.mail.properties.mail.smtp.timeout` (by default is set to `5000`)
  - [**optional**] `spring.mail.properties.mail.smtp.writetimeout` (by default is set to `5000`)
  - [**optional**] `spring.mail.properties.mail.smtp.starttls.enabled` (by default is set to `false`)
- `app.notification.mail.sender` - the sender address that will be used as from in the outgoing mail notifications
- `app.notification.mail.bcc-list` - a list of BCC recipients that should receive a copy of all the outgoing mail notifications

### Plugins

if plugins are to be used, the following startup parameter should be added:
- `-Dloader.path`=[plugins] for using the folder `plugins` next to the jems-server as root to scan for jems plugins

Furthermore, inside `application.yaml`, it can be specified which paths are to be taken for their translation files.
The following parameters are set by default:
```
spring:
   messages:
     basename: classpath:/messages, classpath:/plugin_messages
```
Since all translations for one language end up in the same file, plugin translation files as well as their translation keys, need to use a specific unique plugin identifier!
If the main translation files need to be exchanged, instead of the `classpath:/messages` an own path should be used.

## References
1. [Cloudflight Platform](https://git.internal.cloudflight.io/cloudflight/libs/cloudflight-platform)
2. [Cloudflight Gradle Plugin](https://git.internal.cloudflight.io/cloudflight/gradle/cloudflight-gradle-plugin)
3. [Cloudflight Artifactory](https://artifacts.cloudflight.io/#browse/browse:plugins-maven)
4. [Jems internal development](https://jems-dev.internal.cloudflight.dev)
5. [Jems internal testing](https://stable-jems.interact-eu.net/)
6. [TeamCity](https://teamcity.internal.cloudflight.io/)
7. [Generated Swagger API documentation](https://jems-test.cloudflight.dev/swagger-ui.html#/)

[Cloudflight Platform]: https://git.internal.cloudflight.io/cloudflight/libs/cloudflight-platform
[Cloudflight Gradle Plugin]: https://git.internal.cloudflight.io/cloudflight/gradle/cloudflight-gradle-plugin
[Cloudflight Artifactory]: https://artifacts.cloudflight.io/#browse/browse:plugins-maven
[Jems internal development]: https://jems-dev.internal.cloudflight.dev
[Jems internal testing]: https://stable-jems.interact-eu.net/
[TeamCity]: https://teamcity.internal.cloudflight.io/
[Generated Swagger API documentation]: https://jems-test.cloudflight.dev/swagger-ui.html#/
