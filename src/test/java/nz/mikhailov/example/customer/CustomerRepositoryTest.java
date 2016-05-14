package nz.mikhailov.example.customer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerRepositoryTest {

  @Mock
  private DynamoDBMapper dbMapper;

  @InjectMocks
  private CustomerRepository repository;

  @Test
  @SuppressWarnings("unchecked")
  public void readAllShouldScanTheTable() throws Exception {

    PaginatedScanList expectedResult = mock(PaginatedScanList.class);
    when(dbMapper.scan(eq(Customer.class), any(DynamoDBScanExpression.class))).thenReturn(expectedResult);
    List<Customer> result = repository.readAll();
    assertThat(result, is(expectedResult));
    verify(expectedResult).loadAllResults();
  }

  @Test
  public void readShouldReturnEmptyOptionalWhenNoResult() throws Exception {

    when(dbMapper.load(Customer.class, "Dale Carnegie")).thenReturn(null);
    Optional<Customer> result = repository.read("Dale Carnegie");
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void readShouldWrapResultIntoOptional() throws Exception {

    Customer customer = new Customer().withName("Dale Carnegie");
    when(dbMapper.load(Customer.class, "Dale Carnegie")).thenReturn(customer);
    Customer result = repository.read("Dale Carnegie").get();
    assertThat(result, is(equalTo(customer)));
  }

  @Test
  public void saveShouldPersistCustomer() throws Exception {

    Customer customer = new Customer().withName("Dale Carnegie");
    repository.save(customer);
    verify(dbMapper).save(customer);
  }

  @Test
  public void deleteShouldDeleteCustomerByName() throws Exception {

    repository.delete("Dale Carnegie");
    verify(dbMapper).delete(eq(new Customer().withName("Dale Carnegie")), any(DynamoDBMapperConfig.class));
  }
}