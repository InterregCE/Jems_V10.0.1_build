# kotlin-springboot-angular (KSA)

This skeleton is meant as a template to bootstrap new projects in our standard stack:

* Java / Kotlin
* Spring Boot
* Angular

That all backed by the [Cloudflight Platform](https://git.internal.catalysts.cc/catalysts/cloudflight-platform) and built by the
[Cloudflight Gradle Plugin](https://git.internal.catalysts.cc/catalysts/cloudflight-gradle-plugin).

## Quickstart

1. Copy this locally
2. Search for `CHANGEME` tokens and replace them with your projects information
3. Change the package (`io.cloudflight.skeletons`) to your specific package
4. Run `gradlew clean build` to check if you missed something (and fix errors if any)
5. Add your project's readme instead of this
6. Commit and push to YOUR repository
7. Happy coding! 💙
8. Maybe add yourself to the list of projects who use this. idk. just say'n.

## How to ...

### ... dev this locally

Import in IntelliJ using `gradle` and you will see 3 run configs (you should rename those):

 - `Frontend Build Watch (skeleton-ui)` start the angular build watch (run this for automatic reload after frontend changes, start before Application)
 - `Application` start backend (will run on `:8080`, wait for Frontend to build once before starting)
 - `Debug Angular Application (skeleton-ui)` start intellij JS debugging (optional, you can also debug it via browser devtools)

### ... deploy to prod

Please use our [Teamcity](https://teamcity.internal.catalysts.cc) for this.
Basically run `gradlew clean build` and run the jar found under `./backend/build/libs`.

## Codestyle

Import `./idea/Catalysts codestyle.xml` to your IntelliJ and use it.

## Help

For any questions please contact https://chat.catalysts.cc/channel/sig-skeleton

### Database
This project is now based on [MariaDB](https://mariadb.com/kb/en/installing-and-using-mariadb-via-docker/). The easiest way to start DB for local development is to run it inside docker.
There is a [docker-compose](docker-compose.yml) file, so everything you need is to run `docker-compose up` command in
root directory.
