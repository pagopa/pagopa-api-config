# pagoPa Api Config

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-api-config&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-api-config)

Spring Application exposes Api to manage configuration for EC/PSP on the _Nodo dei Pagamenti_.

- [pagoPa Api Config](#pagopa-api-config)
  - [Api Documentation 📖](#api-documentation-)
  - [Technology Stack](#technology-stack)
  - [Start Project Locally 🚀](#start-project-locally-)
    - [Prerequisites](#prerequisites)
    - [Run docker container](#run-docker-container)
  - [Develop Locally 💻](#develop-locally-)
    - [Prerequisites](#prerequisites-1)
    - [Run the project](#run-the-project)
    - [Spring Profiles](#spring-profiles)
    - [Oracle Docker Container](#oracle-docker-container)
    - [Testing 🧪](#testing-)
      - [Unit testing](#unit-testing)
      - [Integration testing](#integration-testing)
      - [Load testing](#load-testing)
  - [Contributors 👥](#contributors-)
    - [Mainteiners:](#mainteiners)

---
## Api Documentation 📖
See the [Swagger 2 here.](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-api-config/main/openapi/swagger.json)

See the [OpenApi 3 here.](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-api-config/main/openapi/openapi.json)

---

## Technology Stack
- Java 11
- Spring Boot
- Spring Web
- Hibernate
- JPA

---

## Start Project Locally 🚀

### Prerequisites
- docker 
- account on dockerhub

> 👀 The docker account is needed to be able to pull the image oracle-db-ee and for which accept the Terms of Service via web. [Open this link](https://hub.docker.com/_/oracle-database-enterprise-edition) and click on "Proceed to Checkout" button. 

Remember to login to the local docker with `docker login` command

### Run docker container
`docker-compose up --build`

🔴 **Don't worry about error messages, read the whole paragraph**

If all right, eventually you'll see something like that:
```sh
oracle-db-12c | /u01/app/oracle/diag/rdbms/orclcdb/ORCLCDB/trace/ORCLCDB_vktm_29.trc
flyway        | Flyway Community Edition 7.15.0 by Redgate
flyway        | Database: jdbc:oracle:thin:@oracle-db-12c:1521:ORCLCDB (Oracle 12.2)
flyway        | Successfully validated 3 migrations (execution time 00:00.103s)
flyway        | Current version of schema "NODO4_CFG": 2
flyway        | Schema "NODO4_CFG" is up to date. No migration necessary.
flyway exited with code 0
spring-api-config | 2021-09-14 10:54:29.485  INFO 1 --- [           main] i.p.p.a.config.RetryableDataSource       : try getting connection...
spring-api-config | 2021-09-14 10:54:29.485  INFO 1 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
spring-api-config | 2021-09-14 10:54:29.814  INFO 1 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
spring-api-config | 2021-09-14 10:54:29.868  INFO 1 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.Oracle12cDialect
spring-api-config | 2021-09-14 10:55:01.768  INFO 1 --- [           main] i.p.p.a.config.RetryableDataSource       : try getting connection...
spring-api-config | 2021-09-14 10:55:02.065  INFO 1 --- [           main] i.p.p.a.config.RetryableDataSource       : try getting connection...
spring-api-config | 2021-09-14 10:55:02.125  INFO 1 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
spring-api-config | 2021-09-14 10:55:02.159  INFO 1 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
spring-api-config | 2021-09-14 10:55:02.858  WARN 1 --- [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
spring-api-config | 2021-09-14 10:55:03.454  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path '/apiconfig'
spring-api-config | 2021-09-14 10:55:03.468  INFO 1 --- [           main] it.pagopa.pagopa.apiconfig.ApiConfig     : Started ApiConfig in 99.198 seconds (JVM running for 100.391)
```
> NOTE : you can connect to local instance of `NODO4_CFG` db, for example via `sqlplus` typing `sqlplus NODO4_CFG/PASS_NODO4_CFG@localhost:1521/ORCLCDB.localdomain` on a terminal

The `docker-compose` command create 3 containers: 
- `pagopa-api-config` spring application, 
-  Oracle DB with `NODO_CFG` instance
-  and [Flyway](https://flywaydb.org/) to manage db versioning.

The DB container map `/database/volume` directory to store persistent data.
Then flyway updates the DB using the script in `/database/script` directory and the configuration in `/database/conf/flyway.conf`.

Because Oracle container can take a long time to start up, the Spring application tries several times to connect using the annotation `@Retryable` waiting with an increasing delay.
According to [Control startup and shutdown order in Compose](https://docs.docker.com/compose/startup-order/) best practice.
To details see `RetryableDataSource.java` and `RetryableDatabasePostProcessor.java` classes

⚠️ *NB: for this reason you can see some connection error in the application log. After 10 failed attempts the application stops with an error.*

---

## Develop Locally 💻

### Prerequisites
- git
- maven
- jdk-11
- docker

### Run the project
The easiest way to develop locally is start only oracle and flyway containers. 
```
/usr/local/bin/docker-compose up -d oracle
/usr/local/bin/docker-compose up -d flyway
```

Then start the springboot application with this command:

`mvn spring-boot:run -Dspring-boot.run.profiles=local`

Using the spring profile `local`, the Spring application connects to the docker DB.


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

⚠️ **Attention!** 

If you want to connect to DB from your local PC replace `oracle-db-12c` with `localhost`
```
local url = jdbc:oracle:thin:@localhost:1521:ORCLCDB
``` 

### Testing 🧪

#### Unit testing

by `junit` typing `mvn clean verify`
#### Integration testing

by `newman` & `postman` collection 🚀
- automatically  via Azure pipeline ( see `.devops` folder )
- manual typing `bash api-test/setup_env_api_test.sh <YOUR_STAGE> int` example for `localhost`, where `YOUR_STAGE` shoud be one of `{l,d,i,p}`

```
bash api-test/setup_env_api_test.sh l int
```

#### Load testing

by `k6` & `postman` collection 🚀
- automatically  via Azure pipeline ( see `.devops` folder )
- manual typing `bash api-test/setup_env_api_test.sh <YOUR_STAGE> load` example for `localhost`, where `YOUR_STAGE` shoud be one of `{l,d,i,p}`

```
bash api-test/setup_env_api_test.sh l load
```

---

## Contributors 👥
Made with ❤️ from PagoPa S.p.A.

### Mainteiners
See `CODEOWNERS` file

