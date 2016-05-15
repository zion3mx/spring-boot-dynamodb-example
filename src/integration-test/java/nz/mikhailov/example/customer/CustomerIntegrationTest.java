package nz.mikhailov.example.customer;

import nz.mikhailov.example.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest({"server.port=0"})
public class CustomerIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  private RestTemplate restTemplate = new TestRestTemplate();

  @Test
  public void postShouldCreateCustomerAndRespondWithCreated() throws Exception {

    Customer customer = new Customer().withName(randomUUID().toString());
    ResponseEntity<Customer> result = restTemplate.postForEntity(url("/v1/customer"), customer, Customer.class);
    assertThat(result.getStatusCode(), is(CREATED));
    assertThat(result.getBody(), is(equalTo(customer)));
  }

  @Test
  public void postShouldNotCreateCustomerIfAlreadyExistsAndRespondWithConflict() throws Exception {

    Customer customer = new Customer().withName(randomUUID().toString());
    restTemplate.postForEntity(url("/v1/customer"), customer, Customer.class);
    ResponseEntity<Customer> result = restTemplate.postForEntity(url("/v1/customer"), customer, Customer.class);
    assertThat(result.getStatusCode(), is(CONFLICT));
  }

  @Test
  public void getShouldReturnPreviouslyCreatedCustomers() throws Exception {

    Customer customer1 = new Customer().withName(randomUUID().toString());
    Customer customer2 = new Customer().withName(randomUUID().toString());
    restTemplate.postForEntity(url("/v1/customer"), customer1, Customer.class);
    restTemplate.postForEntity(url("/v1/customer"), customer2, Customer.class);
    ResponseEntity<Customer[]> result = restTemplate.getForEntity(url("/v1/customer"), Customer[].class);
    assertThat(result.getStatusCode(), is(OK));
    assertThat(asList(result.getBody()), hasItems(customer1, customer2));
  }

  @Test
  public void getByNameShouldRespondWithNotFoundForCustomerThatDoesNotExist() throws Exception {

    String customerName = randomUUID().toString();
    ResponseEntity<Customer> result = restTemplate.getForEntity(url("/v1/customer/" + customerName), Customer.class);
    assertThat(result.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void getByNameShouldReturnPreviouslyCreatedCustomer() throws Exception {

    String customerName = randomUUID().toString();
    Customer customer = new Customer().withName(customerName);
    restTemplate.postForEntity(url("/v1/customer"), customer, Customer.class);
    ResponseEntity<Customer> result = restTemplate.getForEntity(url("/v1/customer/" + customerName), Customer.class);
    assertThat(result.getStatusCode(), is(OK));
    assertThat(result.getBody(), is(equalTo(customer)));
  }

  @Test
  public void putShouldReplyWithNotFoundForCustomerThatDoesNotExist() throws Exception {

    String customerName = randomUUID().toString();
    Customer customer = new Customer().withName(customerName);
    RequestEntity<Customer> request = new RequestEntity<>(customer, PUT, url("/v1/customer/" + customerName));
    ResponseEntity<Customer> result = restTemplate.exchange(request, Customer.class);
    assertThat(result.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void putShouldReplaceExistingCustomerValues() throws Exception {

    String customerName = randomUUID().toString();
    Customer oldCustomerData = new Customer().withName(customerName).withPhoneNumber("1234567890");
    Customer newCustomerData = new Customer().withName(customerName).withAddress("New Zealand");
    restTemplate.postForEntity(url("/v1/customer"), oldCustomerData, Customer.class);
    RequestEntity<Customer> request = new RequestEntity<>(newCustomerData, PUT, url("/v1/customer/" + customerName));
    ResponseEntity<Customer> result = restTemplate.exchange(request, Customer.class);
    assertThat(result.getStatusCode(), is(OK));
    assertThat(result.getBody(), is(equalTo(newCustomerData)));
  }

  @Test
  public void patchShouldReplyWithNotFoundForCustomerThatDoesNotExist() throws Exception {

    String customerName = randomUUID().toString();
    Customer customer = new Customer().withName(customerName);
    RequestEntity<Customer> request = new RequestEntity<>(customer, PATCH, url("/v1/customer/" + customerName));
    ResponseEntity<Customer> result = restTemplate.exchange(request, Customer.class);
    assertThat(result.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void patchShouldAddNewValuesToExistingCustomerValues() throws Exception {

    String customerName = randomUUID().toString();
    Customer oldCustomerData = new Customer().withName(customerName).withPhoneNumber("1234567890");
    Customer newCustomerData = new Customer().withName(customerName).withAddress("New Zealand");
    Customer expectedNewCustomerData = new Customer().withName(customerName).withAddress("New Zealand").withPhoneNumber("1234567890");
    restTemplate.postForEntity(url("/v1/customer"), oldCustomerData, Customer.class);
    RequestEntity<Customer> request = new RequestEntity<>(newCustomerData, PATCH, url("/v1/customer/" + customerName));
    ResponseEntity<Customer> result = restTemplate.exchange(request, Customer.class);
    assertThat(result.getStatusCode(), is(OK));
    assertThat(result.getBody(), is(equalTo(expectedNewCustomerData)));
  }

  @Test
  public void deleteShouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {

    String customerName = randomUUID().toString();
    RequestEntity<Void> request = new RequestEntity<>(DELETE, url("/v1/customer/" + customerName));
    ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
    assertThat(result.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void deleteShouldRemoveExistingCustomerAndRespondWithNoContent() throws Exception {

    String customerName = randomUUID().toString();
    Customer customer = new Customer().withName(customerName);
    restTemplate.postForEntity(url("/v1/customer"), customer, Customer.class);
    RequestEntity<Void> request = new RequestEntity<>(DELETE, url("/v1/customer/" + customerName));
    ResponseEntity<Void> result = restTemplate.exchange(request, Void.class);
    assertThat(result.getStatusCode(), is(NO_CONTENT));
  }

  private URI url(String url) {

    return URI.create("http://localhost:" + port + url);
  }
}
