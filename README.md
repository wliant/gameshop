## Prerequisite
 - Gradle 7.3.1
 - JDK 17
 - docker (mysql)

## How to run
 - `docker-compose -f docker/docker-compose.yaml up -d`
 - `gradle clean bootJar`
 - `java -jar build/libs/gameshop-0.0.1-SNAPSHOT.jar`

