package nz.mikhailov.example.customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

  @Mock
  private CustomerRepository repository;

  @InjectMocks
  private CustomerService service;

  @Test
  public void readShouldReturnEmptyOptionalWhenNoCustomerFound() throws Exception {

    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.empty());
    Optional<Customer> result = service.read("Arthur C. Clarke");
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void readShouldReturnResultWhenCustomerFound() throws Exception {

    Customer customer = new Customer().withName("Arthur C. Clarke");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(customer));
    Customer result = service.read("Arthur C. Clarke").get();
    assertThat(result, is(equalTo(customer)));
  }

  @Test
  public void createShouldReturnEmptyOptionalWhenCustomerAlreadyExists() throws Exception {

    Customer existingCustomer = new Customer().withName("Arthur C. Clarke").withAddress("Sri Lanka");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(existingCustomer));
    Customer newCustomer = new Customer().withName("Arthur C. Clarke");
    Optional<Customer> result = service.create(newCustomer);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newCustomer);
  }

  @Test
  public void createShouldReturnNewCustomerWhenCustomerNotYetExists() throws Exception {

    Customer newCustomer = new Customer().withName("Arthur C. Clarke");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.empty());
    Customer result = service.create(newCustomer).get();
    assertThat(result, is(equalTo(newCustomer)));
    verify(repository).save(newCustomer);
  }

  @Test
  public void replaceShouldReturnEmptyOptionalWhenCustomerNotFound() throws Exception {

    Customer newCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("Sri Lanka");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.empty());
    Optional<Customer> result = service.replace(newCustomerData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newCustomerData);
  }

  @Test
  public void replaceShouldOverwriteAndReturnNewDataWhenCustomerExists() throws Exception {

    Customer oldCustomerData = new Customer().withName("Arthur C. Clarke").withPhoneNumber("000000");
    Customer newCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("Sri Lanka");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(oldCustomerData));
    Customer result = service.replace(newCustomerData).get();
    assertThat(result, is(equalTo(newCustomerData)));
    verify(repository).save(newCustomerData);
  }

  @Test
  public void updateShouldReturnEmptyOptionalWhenCustomerNotFound() throws Exception {

    Customer newCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("Sri Lanka");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.empty());
    Optional<Customer> result = service.update(newCustomerData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newCustomerData);
  }

  @Test
  public void updateShouldOverwriteExistingFieldAndReturnNewDataWhenCustomerExists() throws Exception {

    Customer oldCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("England");
    Customer newCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("Sri Lanka");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(oldCustomerData));
    Customer result = service.update(newCustomerData).get();
    assertThat(result, is(equalTo(newCustomerData)));
    verify(repository).save(newCustomerData);
  }

  @Test
  public void updateShouldNotOverwriteExistingFieldIfNoNewValuePassedAndShouldReturnNewDataWhenCustomerExists() throws Exception {

    Customer oldCustomerData = new Customer().withName("Arthur C. Clarke").withAddress("England");
    Customer newCustomerData = new Customer().withName("Arthur C. Clarke").withPhoneNumber("000000");
    Customer expectedResult = new Customer().withName("Arthur C. Clarke").withAddress("England").withPhoneNumber("000000");
    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(oldCustomerData));
    Customer result = service.update(newCustomerData).get();
    assertThat(result, is(equalTo(expectedResult)));
    verify(repository).save(expectedResult);
  }

  @Test
  public void deleteShouldReturnFalseWhenCustomerNotFound() throws Exception {

    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.empty());
    boolean result = service.delete("Arthur C. Clarke");
    assertThat(result, is(false));
  }

  @Test
  public void deleteShouldReturnTrueWhenCustomerDeleted() throws Exception {

    when(repository.read("Arthur C. Clarke")).thenReturn(Optional.of(new Customer().withName("Arthur C. Clarke")));
    boolean result = service.delete("Arthur C. Clarke");
    assertThat(result, is(true));
    verify(repository).delete("Arthur C. Clarke");
  }

  @Test
  public void listShouldReturnEmptyListWhenNothingFound() throws Exception {

    when(repository.readAll()).thenReturn(emptyList());
    List<Customer> result = service.list();
    assertThat(result, is(emptyCollectionOf(Customer.class)));
  }

  @Test
  public void listShouldReturnAllCustomers() throws Exception {

    Customer customer1 = new Customer().withName("Arthur C. Clarke");
    Customer customer2 = new Customer().withName("Dale Carnegie");
    when(repository.readAll()).thenReturn(asList(customer1, customer2));
    List<Customer> result = service.list();
    assertThat(result, containsInAnyOrder(customer1, customer2));
  }
}