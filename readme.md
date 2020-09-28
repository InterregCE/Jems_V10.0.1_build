# Monitoring System


This application is build on top of an kotlin-springboot-angular template with:

* Java / Kotlin
* Spring Boot
* Angular

## How to ...

### ... dev this locally

Import in IntelliJ using `gradle` and you will see 3 run configs (you should rename those):

 - `Frontend Build Watch (skeleton-ui)` start the angular build watch (run this for automatic reload after frontend changes, start before Application)
 - `Application` start backend (will run on `:8080`, wait for Frontend to build once before starting)
 - `Debug Angular Application (skeleton-ui)` start intellij JS debugging (optional, you can also debug it via browser devtools)

### ... deploy to prod

Basically run `gradlew clean build` and run the jar found under `./backend/build/libs`.

## Codestyle

Import `./idea/Cloudflight-codestyle.xml` to your IntelliJ and use it.

## Help

For any questions please contact the developers.

### Database (Store)
This project is now based on
- [MariaDB](https://mariadb.com/kb/en/installing-and-using-mariadb-via-docker/) for storing all data
- ElasticSearch for storing "audit logs". Those are tracking all interactions between users and system itself.
- MinIO file storage for storing files

The easiest way to start everything needed for local development is to run it inside docker.
There is a [docker-compose](docker-compose.yml) file, so everything you need is to run `docker-compose up` command in
root directory. For intelliJ there are also configurations
- [ems-database](.idea/runConfigurations/ems_database.xml) to start MariaDB inside Docker
- [ems-audit](.idea/runConfigurations/ems_audit.xml) to start ElasticSearch and Kibana inside Docker. Those data are
then available either directly in ElasticSearch [localhost:9200](http://localhost:9200/audit-log/audit/_search) or
in Kibana web interface [localhost:5601](http://localhost:5601)
- [ems-minio](.idea/runConfigurations/ems_minio.xml) to start MinIO file storage. MinIO is then accessible on
[localhost:9000](http://localhost:9000)

There is no need to start everything besides MariaDB, so spring-boot will also start without MinIO and without
Audit (ES+Kibana). There is property value `audit-service.enabled` - if you set to `false` audits will be written
as _info \[LOG\]_ to stdout.

### API testing
For testing APIs we introduced module [ems-rest-test](ems-rest-test), see documentation there.
