# OAuth

OAuth

```bash
mvn install:install-file -DproxySet=true -Dfile=ojdbc6-12.1.0.2.jar -DgroupId=com.oracle  -DartifactId=oracle -Dversion=12.1.0.2 -Dpackaging=jar -DgeneratePom=true
```

```bash
mvn deploy:deploy-file -DgroupId=com.oracle -DartifactId=oracle -Dversion=12.1.0.2 -Durl=file:./maven-repository/ -DrepositoryId=local-maven-repository -DupdateReleaseInfo=true -Dfile=ojdbc6-12.1.0.2.jar
```