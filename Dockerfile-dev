ARG HTTP_PROXY

FROM adigitalteam/springbootbase as builder
#ENV http_proxy http://localhost
#ENV https_proxy http://localhost
#ENV no_proxy 127.0.0.1,app,localhost
RUN mkdir /application
WORKDIR /application
COPY src src
COPY pom.xml pom.xml
COPY ojdbc6-12.1.0.2.jar ojdbc6-12.1.0.2.jar
#RUN mvn install:install-file -DproxySet=true -DproxyHost=localhost -DproxyPort=8080 -Dfile=ojdbc6-12.1.0.2.jar -DgroupId=com.oracle  -DartifactId=oracle -Dversion=12.1.0.2 -Dpackaging=jar -DgeneratePom=true
RUN mvn install:install-file -DproxySet=true -Dfile=ojdbc6-12.1.0.2.jar -DgroupId=com.oracle  -DartifactId=oracle -Dversion=12.1.0.2 -Dpackaging=jar -DgeneratePom=true
#RUN mvn package -DproxySet=true -DproxyHost=localhost -DproxyPort=8080
RUN mvn package -DproxySet=true

FROM openjdk:11.0.7-jre
#ENV http_proxy ${HTTP_PROXY}
#ENV https_proxy ${HTTP_PROXY}
#ENV no_proxy 127.0.0.1,app,localhost
COPY --from=builder /application/target/*.jar /app/lib/application.jar
WORKDIR /app/lib
ENTRYPOINT ["java", "-jar", "-Doracle.jdbc.timezoneAsRegion=false", "application.jar"]
