# pagopa-api-config
Spring application to manage configuration Api for EC/PSP on the Nodo component.

## Start Project Locally
If you just want to try this project, open a terminal in the root of this project and run:

`docker-compose up --build`

This command create 3 containers: the Spring application, the Oracle DB and [Flyway](https://flywaydb.org/).
The DB container map `/database/volume` directory to store persistent data.
Then flyway updates the DB using the script in `/database/script` directory and the configuration in `/database/conf/flyway.conf`.

Because Oracle container can take a long time to start up, the Spring application tries several times to connect using the annotation `@Retryable` waiting with an increasing delay.

See these classes:
```
/src/main/java/it/pagopa/pagopa/apiconfig/config/RetryableDataSource.java
/src/main/java/it/pagopa/pagopa/apiconfig/config/RetryableDatabasePostProcessor.java
```

*NB: for this reason you can see some connection error in the application log. After 10 failed attempts the application stops with an error.*

## Develop Locally
The easiest way to develop locally is start only oracle and flyway containers. 
```
/usr/local/bin/docker-compose up -d oracle
/usr/local/bin/docker-compose up -d flyway
```

Then start the springboot application with this command:

`mvn spring-boot:run -Dspring-boot.run.profiles=local`

Using the spring profile `local` the Spring application connects to the docker DB.


### Spring Profiles

- _no-profile_: to run in Azure
- **local**: to develop locally using the docker db.
- **docker**: profile used by the app inside the container (see: `/.env` file)
- **sia**: to develop locally if you want to connect to SIA database (using VPN)


### Oracle Docker Container
Connection info of DB from other docker containers:
```
url = jdbc:oracle:thin:@oracle-db-12c:1521:ORCLCDB
user = NODO4_CFG
password = PSS_NODO4_CFG
```

**Attention!** 

If you want to connect to DB from your local PC replace `oracle-db-12c` with `localhost`
```
local url = jdbc:oracle:thin:@localhost:1521:ORCLCDB
``` 

## Contributors
Make with ❤️ from PagoPa S.p.A.

### Mainteiners:
- [Jacopo Carlini](https://github.com/jacopocarlini)
- [Pasquale Spica](https://github.com/pasqualespica)
