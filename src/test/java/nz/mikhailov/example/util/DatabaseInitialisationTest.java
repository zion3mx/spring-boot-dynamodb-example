package nz.mikhailov.example.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import nz.mikhailov.example.customer.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.amazonaws.services.dynamodbv2.model.TableStatus.ACTIVE;
import static com.amazonaws.services.dynamodbv2.model.TableStatus.CREATING;
import static com.amazonaws.services.dynamodbv2.model.TableStatus.DELETING;
import static com.amazonaws.services.dynamodbv2.model.TableStatus.UPDATING;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseInitialisationTest {

  @Mock
  private DynamoDBMapper dbMapper;

  @Mock
  private AmazonDynamoDB dynamoDB;
  
  @InjectMocks
  private DatabaseInitialisation databaseInitialisation;

  private final String tableName = "Customer";
  private final CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName);

  @Before
  public void setUp() throws Exception {

    when(dbMapper.generateCreateTableRequest(Customer.class)).thenReturn(createTableRequest);
    when(dynamoDB.createTable(createTableRequest))
        .thenReturn(new CreateTableResult().withTableDescription(tableDescriptionWithStatus(CREATING)));
  }

  @Test
  public void shouldNotCreateTableIfAlreadyExists() throws Exception {

    when(dynamoDB.describeTable(tableName))
        .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(ACTIVE)));
    databaseInitialisation.onApplicationEvent(null);
    verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
  }

  @Test
  public void shouldNotCreateTableIfTableCreationInProgress() throws Exception {

    when(dynamoDB.describeTable(tableName))
        .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(CREATING)));
    databaseInitialisation.onApplicationEvent(null);
    verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
  }

  @Test
  public void shouldNotCreateTableIfTableDeletionInProgress() throws Exception {

    when(dynamoDB.describeTable(tableName))
        .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(DELETING)));
    databaseInitialisation.onApplicationEvent(null);
    verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
  }

  @Test
  public void shouldNotCreateTableIfTableUpdateInProgress() throws Exception {

    when(dynamoDB.describeTable(tableName))
        .thenReturn(new DescribeTableResult().withTable(tableDescriptionWithStatus(UPDATING)));
    databaseInitialisation.onApplicationEvent(null);
    verify(dynamoDB, never()).createTable(any(CreateTableRequest.class));
  }

  @Test
  public void shouldCreateTableIfTableDoesNotExist() throws Exception {

    when(dynamoDB.describeTable(tableName)).thenThrow(new ResourceNotFoundException("Simulated failure"));
    databaseInitialisation.onApplicationEvent(null);
    verify(dynamoDB).createTable(createTableRequest);
  }

  private TableDescription tableDescriptionWithStatus(TableStatus status) {

    return new TableDescription().withTableStatus(status).withTableName(tableName);
  }

}