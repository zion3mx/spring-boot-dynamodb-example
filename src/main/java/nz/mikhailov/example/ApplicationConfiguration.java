package nz.mikhailov.example;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

  private final static String DYNAMODB_ENDPOINT_DEFAULT_VALUE = "http://localhost:8000";

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${dynamoDbEndpoint:" + DYNAMODB_ENDPOINT_DEFAULT_VALUE + "}")
  private String dynamoDbEndpoint;

  @Bean
  public AmazonDynamoDB amazonDynamoDb() {

    log.trace("Entering amazonDynamoDb()");
    AmazonDynamoDB client = new AmazonDynamoDBClient();
    log.info("Using DynamoDb endpoint {}", dynamoDbEndpoint);
    client.setEndpoint(dynamoDbEndpoint);
    return client;
  }

}
