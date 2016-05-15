# Spring Boot RESTful Microservice Example with Amazon DynamoDB

## The original task

> Write an application that provides CRUD (create, read, update, delete) RESTful services for customer data.
>
> Each customer has a
> - name
> - address
> - telephone number
>
> The code should be production level quality and it should be possible to run the application.
>
> Further, the code should demonstrate your level of proficiency in Test Driven Development.
>
> The technology to be used:
> - Java 7 or later
> - free choice of Java frameworks

## Decisions made

* Java 8
* Customer Name is chosen to be Primary Key for the data (instead of generated identifier).
This will help demonstrate "user already exists" kind of errors in API.
* Spring Framework stack chosen as the most popular one.
* AWS DynamoDB database used as very easy to use, managed, and extremely cheap NoSQL database.

## To run the application locally

### To run on default ports

Default ports are `8000` (for DynamoDB-Local) and `8080` (for Spring Boot Embedded Tomcat)

0. Install Java 8

1. Download and run DynamoDB-Local from [Amazon Website](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html#DynamoDBLocal.DownloadingAndRunning) with the following parameters

        java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory

2. After cloning current repository run the app

        ./gradlew clean bootRun

### To run on different ports

The following example will let you start DynamoDB-Local on port `8881` and the application on port `8882`:

1. To start DynamoDb-Local

        java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory -port 8881

2. To start the application

        ./gradlew clean bootRepackage
        java -jar -DdynamoDbEndpoint=http://localhost:8881 -Dserver.port=8882 build/libs/spring-rest-dynamodb-example.jar

### To run unit tests

    ./gradlew clean test

Unit test report will be in `./build/reports/tests/index.html`

### To run integration tests

Following command line assumes that you are running DynamoDB-Local on port 8000

    ./gradlew clean integrationTest

## REST API

### Health Check

    GET /health

HTTP Response `200 OK` considered as healthy

Example:

    curl -i -X GET http://localhost:8080/health
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 2
    Date: Sat, 14 May 2016 09:30:24 GMT

    up

### List all customers

    GET /v1/customer

Returns the list of all customers.
Returns `204 NO CONTENT` if database is empty, `200 OK` if results present, and other standard HTTP response codes.

Example:

    curl -i -X GET http://localhost:8080/v1/customer
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sat, 14 May 2016 09:54:28 GMT

    [{"name":"Arthur Conan Doyle","address":"Crowborough, United Kingdom","phoneNumber":"+440000000"},{"name":"Arthur C. Clarke","address":"Colombo, Sri Lanka","phoneNumber":null}]

### Create new customer

    POST /v1/customer
    BODY: Json Object
    HEADERS: Content-Type: application/json

Creates new customer.
Returns `201 CREATED` in case of success, `409 CONFLICT` in case if customer already exists, and other standard HTTP response codes.

Example:

    curl -i -X POST http://localhost:8080/v1/customer \
    	-H "Content-Type: application/json" \
    	-d $'{
      "name": "Olaf Stapledon",
      "address": "Seacombe, United Kingdom",
      "phoneNumber": "+440000000"
    }'
    HTTP/1.1 201 Created
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sat, 14 May 2016 09:39:26 GMT

    {"name":"Olaf Stapledon","address":"Seacombe, United Kingdom","phoneNumber":"+440000000"}

### Read customer by name

    GET /v1/customer/{customer_name_url_encoded}

Returns customer by name.
Returns `200 OK` in case of success, `404 NOT FOUND` if customer does not exist, and other standard HTTP response codes.

Example:

    curl -i -X GET http://localhost:8080/v1/customer/Olaf%20Stapledon
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sat, 14 May 2016 09:42:24 GMT

    {"name":"Olaf Stapledon","address":"Seacombe, United Kingdom","phoneNumber":"+440000000"}

### Update (replace whole customer object) by name

    PUT /v1/customer/{customer_name_url_encoded}
    BODY: Json Object
    HEADERS: Content-Type: application/json

Replaces customer object by name.
Returns `200 OK` in case of success, `404 NOT FOUND` if customer does not exist, and other standard HTTP response codes.

Example (removed phone number):

    curl -i -X PUT http://localhost:8080/v1/customer/Olaf%20Stapledon \
        	-H "Content-Type: application/json" \
        	-d $'{
          "address": "Seacombe, United Kingdom"
        }'
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sat, 14 May 2016 09:46:46 GMT

    {"name":"Olaf Stapledon","address":"Seacombe, United Kingdom","phoneNumber":null}

### Partial Update (overwrites only passed fields) by name

    PATCH /v1/customer/{customer_name_url_encoded}
    BODY: Json Object
    HEADERS: Content-Type: application/json

Replaces customer object by name.
Returns `200 OK` in case of success, `404 NOT FOUND` if customer does not exist, and other standard HTTP response codes.

Example:

    curl -i -X PATCH http://localhost:8080/v1/customer/Olaf%20Stapledon \
            -H "Content-Type: application/json" \
            -d $'{
          "address": "Caldy, Cheshire, England, UK"
        }'
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    Transfer-Encoding: chunked
    Date: Sat, 14 May 2016 09:49:29 GMT

    {"name":"Olaf Stapledon","address":"Caldy, Cheshire, England, UK","phoneNumber":null}

### Delete by name

    DELETE /v1/customer/{customer_name_url_encoded}

Removes customer by name.
Returns `204 NO CONTENT` in case of success, `404 NOT FOUND` if customer does not exist, and other standard HTTP response codes.

    curl -i -X DELETE http://localhost:8080/v1/customer/Olaf%20Stapledon
    HTTP/1.1 204 No Content
    Server: Apache-Coyote/1.1
    Date: Sat, 14 May 2016 09:51:46 GMT

