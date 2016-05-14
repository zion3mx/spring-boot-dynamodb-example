FROM java:alpine

COPY build/libs/spring-rest-dynamodb-example.jar ./

EXPOSE 8080