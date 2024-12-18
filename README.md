# MDD

A simple app with a Java backend and Angular frontend.
This is the sixth project of my course as a full-stack Java / Angular developer with OpenClassrooms.
The goal here is to write a full app with separated front and backend.

# Installation
## Requirements
- Java 23
- Access to a MySQL (or MariaDB) server and a database
- Maven
- Git
- Postman for testing purposes
- Mockoon to run a mock Restful API as backend if needed

## Installation
1. Clone this repository on your local machine or server and go to directory

``` bash
git clone https://github.com/wilzwert/MDD.git
cd savasana
```

2. Configuration

This application uses a .env file to configure most of its parameters.
Please copy the provided .env.example file to .env

Linux / unix :
``` bash 
cp .env.example .env
```

Windows
``` bash 
copy .env.example .env
```

Then setup variables according to your environment ; see .env.example for more information.
Please note that there is a slight difference whether you're using MySQL or MariaDB, although default values should work for both.

OPTIONAL - Import the schema and data provided in resources/sql into your database.

Starting the backend should create database tables, however you can create the database schema by importing the file provided in resources/sql/db_structure.sql

You can either use a GUI like PhpMyAdmin or load the file from command line :

``` bash
 mysql -u user -p database_name < ressources/db_structure.sql
 ```
You can also populate the database with test data by importing the file provided in resources/sql/reset_data.sql
Please note that this script also clears all previous data.

``` bash
 mysql -u user -p database_name < ressources/db_structure.sql
 ```

3. Install backend dependencies

``` bash
cd back
mvn clean install
```

Or directly in your favorite IDE

4. Install frontend dependencies

``` bash
cd front
npm install
```

# Start the backend API

Windows :
``` bash 
cd back
mvnw spring-boot:run
```

Linux / unix :
``` bash
cd back 
mvn spring-boot:run
```

This will make the API available on localhost:8080.

# Start the frontend application

- run frontend
``` bash 
cd front
npm run start
```
or
``` bash 
cd front
ng serve
```
This will make the frontend available on localhost:4200.

Should you wish to run the frontend on a "mock" API, you can import the file provided in resources/mockoon/mdd.json and edit proxy configuration in front/proxy.conf.json to reflect your Mockoon API host and port.  

# Test the API
1. With provided Postman collection : see resources/postman/MDDApi.postman_collection.json

Import the collection in Postman : https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman

Then run the collection.

# API documentation

Once the backend application is started, you should be able to access the API Documentation (please adjust host and port to your environment).
> http://localhost:8080/swagger-ui/index.html

If you set a custom SWAGGER_PATH in .env, for example
> SWAGGER_PATH=/api-doc
> 
> You can also access Swagger UI on
> http://localhost:8080/api-doc

Please note that even if you did set a custom Swagger path, you will be redirected to /swagger-ui/index.html

# Testing and code coverage
## Backend

Unitary and integration tests are located in back/src/test/java/com.openclassrooms.mdd.
Unitary tests classes names are suffixed by 'Test', integration tests classes names are suffixed by 'IT'.

> cd back

### Unit tests and jacoco report

Unitary tests are run with Maven Surefire Plugin.

> mvn clean test

Jacoco report with code coverage is located in target/site/jacoco-ut/index.html

### Unit and integration tests with jacoco report

Integration tests are run with Maven Failsafe Plugin.

> mvn clean verify

Jacoco report with code coverage is located in target/site/jacoco-merged/index.html

## Frontend

### Unitary and integration tests

Unitary tests filenames are suffixed with .spec.ts.

Integration tests filenames are suffixed with .integration.spec.ts.

Launching tests:

> npm run test

for following change:

> npm run test:watch

Launch unitary and integration tests with coverage report generation

> npm run test:coverage

Report is available here:

> front/coverage/jest/lcov-report/index.html

### E2E tests with Cypress

End-to-end tests files are located in front/cypress/e2e.

Launching e2e tests manually with Cypress UI :

> npm run e2e

Launching all e2e tests automatically in CLI (headless) with report generation :

> "npm run e2e:ci"

Report is available here:

> front/coverage/e2e/lcov-report/index.html

# Features
## Security
- JWT Token authentication
- Request data validation through DTO

## Persistence
- Entities relationships
- Cascade handling

## Error handling, logging
- Global exception handler generating Http responses with appropriate Http status codes
- Logging levels can be configured in src/main/resources/application.properties. Defaults:

```
logging.level.root=warn
logging.level.org.springframework.security=warn
logging.level.com.openclassrooms.mdd=error
logging.level.com.openclassrooms.mdd.service=error
logging.level.com.openclassrooms.mdd.controller=error
logging.level.org.hibernate.SQL = warn
```

Level 'info' for services and controllers can get very verbose under certain circumstances  :)

# TODO / Improvements
## Javadoc
Javadoc may be improved by better commenting backend code
As of now, you can generate javadoc by running 

> cd back
> mvn javadoc:javadoc

Javadoc should be available in target/site/apidocs/index.html

## Frontend doc
Frontend code could and should be better documented as well.

## Use of signals and/or store
As of now, local cache of data (user subscriptions, current user...) are handled by observables exposed in or subscribed by services which in turn
observe the user logged status to clear themselves when needed. 

This may be better handled by the use of Signals and/or a central store to manage data.

## Pagination
Implementing a pagination, especially in the "Post" page, could become mandatory to improve UX as the the article number grows. 

