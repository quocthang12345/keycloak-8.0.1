# Keycloak Quarkus Distribution

Keycloak trên Quarkus là một công việc truy cập. 

## Building

    mvn -f ../pom.xml clean install -DskipTestsuite -DskipExamples -DskipTests -Pquarkus

## Running

    java -jar server/target/keycloak-runner.jar
    
## Running in dev mode

    cd server
    mvn compile quarkus:dev

