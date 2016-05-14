package nz.mikhailov.example.customer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private DynamoDBMapper dbMapper;

  public List<Customer> readAll() {

    log.trace("Entering readAll()");
    PaginatedList<Customer> results = dbMapper.scan(Customer.class, new DynamoDBScanExpression());
    results.loadAllResults();
    return results;
  }

  public Optional<Customer> read(String name) {

    log.trace("Entering read() with {}", name);
    return Optional.ofNullable(dbMapper.load(Customer.class, name));
  }

  public void save(Customer customer) {

    log.trace("Entering save() with {}", customer);
    dbMapper.save(customer);
  }

  public void delete(String name) {

    dbMapper.delete(new Customer().withName(name), new DynamoDBMapperConfig(SaveBehavior.CLOBBER));
  }
}
