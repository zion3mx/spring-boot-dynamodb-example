package nz.mikhailov.example.customer;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest {

  @Mock
  private CustomerService service;

  @InjectMocks
  private CustomerController controller;

  @Test
  public void listShouldRespondWithNoContentWhenNothingInDatabase() throws Exception {

    when(service.list()).thenReturn(emptyList());
    ResponseEntity<List<Customer>> result = controller.list();
    assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
  }

  @Test
  public void listShouldRespondWithOkAndResultsFromService() throws Exception {

    Customer customer1 = new Customer().withName("Conan Doyle");
    Customer customer2 = new Customer().withName("Olaf Stapledon");
    when(service.list()).thenReturn(asList(customer1, customer2));
    ResponseEntity<List<Customer>> result = controller.list();
    assertThat(result, is(allOf(
        responseEntityWithStatus(OK),
        responseEntityThat(containsInAnyOrder(customer1, customer2)))));
  }

  @Test
  public void readShouldReplyWithNotFoundIfNoSuchCustomer() throws Exception {

    when(service.read("Olaf Stapledon")).thenReturn(Optional.empty());
    ResponseEntity<Customer> result = controller.read("Olaf Stapledon");
    assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
  }

  @Test
  public void readShouldReplyWithCustomerIfCustomerExists() throws Exception {

    Customer customer = new Customer().withName("Olaf Stapledon");
    when(service.read("Olaf Stapledon")).thenReturn(Optional.of(customer));
    ResponseEntity<Customer> result = controller.read("Olaf Stapledon");
    assertThat(result, is(allOf(
        responseEntityWithStatus(OK),
        responseEntityThat(equalTo(customer)))));
  }

  @Test
  public void createShouldReplyWithConflictIfCustomerAlreadyExists() throws Exception {

    Customer customer = new Customer().withName("Olaf Stapledon");
    when(service.create(customer)).thenReturn(Optional.empty());
    ResponseEntity<Customer> result = controller.create(customer);
    assertThat(result, is(responseEntityWithStatus(CONFLICT)));
  }

  @Test
  public void createShouldReplyWithCreatedAndCustomerData() throws Exception {

    Customer customer = new Customer().withName("Olaf Stapledon");
    when(service.create(customer)).thenReturn(Optional.of(customer));
    ResponseEntity<Customer> result = controller.create(customer);
    assertThat(result, is(allOf(
        responseEntityWithStatus(CREATED),
        responseEntityThat(equalTo(customer)))));
  }

  @Test
  public void putShouldReplyWithNotFoundIfCustomerDoesNotExist() throws Exception {

    Customer newCustomerData = new Customer().withName("Olaf Stapledon").withAddress("England");
    when(service.replace(newCustomerData)).thenReturn(Optional.empty());
    ResponseEntity<Customer> result = controller.put("Olaf Stapledon", new Customer().withAddress("England"));
    assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
  }

  @Test
  public void putShouldReplyWithUpdatedCustomerAndOkIfCustomerExists() throws Exception {

    Customer newCustomerData = new Customer().withName("Olaf Stapledon").withAddress("England");
    when(service.replace(newCustomerData)).thenReturn(Optional.of(newCustomerData));
    ResponseEntity<Customer> result = controller.put("Olaf Stapledon", new Customer().withAddress("England"));
    assertThat(result, is(allOf(
        responseEntityWithStatus(OK),
        responseEntityThat(equalTo(newCustomerData)))));
  }

  @Test
  public void patchShouldReplyWithNotFoundIfCustomerDoesNotExist() throws Exception {

    Customer newCustomerData = new Customer().withName("Olaf Stapledon").withAddress("England");
    when(service.update(newCustomerData)).thenReturn(Optional.empty());
    ResponseEntity<Customer> result = controller.patch("Olaf Stapledon", new Customer().withAddress("England"));
    assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
  }

  @Test
  public void patchShouldReplyWithUpdatedCustomerAndOkIfCustomerExists() throws Exception {

    Customer newCustomerData = new Customer().withName("Olaf Stapledon").withAddress("England");
    when(service.update(newCustomerData)).thenReturn(Optional.of(newCustomerData));
    ResponseEntity<Customer> result = controller.patch("Olaf Stapledon", new Customer().withAddress("England"));
    assertThat(result, is(allOf(
        responseEntityWithStatus(OK),
        responseEntityThat(equalTo(newCustomerData)))));
  }

  @Test
  public void deleteShouldRespondWithNotFoundIfCustomerDoesNotExist() throws Exception {

    when(service.delete("Olaf Stapledon")).thenReturn(false);
    ResponseEntity<Void> result = controller.delete("Olaf Stapledon");
    assertThat(result, is(responseEntityWithStatus(NOT_FOUND)));
  }

  @Test
  public void deleteShouldRespondWithNoContentIfDeleteSuccessful() throws Exception {

    when(service.delete("Olaf Stapledon")).thenReturn(true);
    ResponseEntity<Void> result = controller.delete("Olaf Stapledon");
    assertThat(result, is(responseEntityWithStatus(NO_CONTENT)));
  }

  private Matcher<ResponseEntity> responseEntityWithStatus(HttpStatus status) {

    return new TypeSafeMatcher<ResponseEntity>() {

      @Override
      protected boolean matchesSafely(ResponseEntity item) {

        return status.equals(item.getStatusCode());
      }

      @Override
      public void describeTo(Description description) {

        description.appendText("ResponseEntity with status ").appendValue(status);
      }
    };
  }

  private <T> Matcher<ResponseEntity<? extends T>> responseEntityThat(Matcher<T> categoryMatcher) {

    return new TypeSafeMatcher<ResponseEntity<? extends T>>() {
      @Override
      protected boolean matchesSafely(ResponseEntity<? extends T> item) {

        return categoryMatcher.matches(item.getBody());
      }

      @Override
      public void describeTo(Description description) {

        description.appendText("ResponseEntity with ").appendValue(categoryMatcher);
      }
    };
  }
}