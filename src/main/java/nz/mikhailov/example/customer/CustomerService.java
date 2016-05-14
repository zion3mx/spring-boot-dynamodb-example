package nz.mikhailov.example.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;

@Service
public class CustomerService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private CustomerRepository repository;

  public Optional<Customer> read(String name) {

    log.trace("Entering read() with {}", name);
    return repository.read(name);
  }

  public Optional<Customer> create(Customer customer) {

    log.trace("Entering create() with {}", customer);
    if (repository.read(customer.getName()).isPresent()) {
      log.warn("Customer {} not found", customer.getName());
      return Optional.empty();
    }
    repository.save(customer);
    return Optional.of(customer);
  }

  public Optional<Customer> replace(Customer newCustomerData) {

    log.trace("Entering replace() with {}", newCustomerData);
    Optional<Customer> existingCustomer = repository.read(newCustomerData.getName());
    if (!existingCustomer.isPresent()) {
      log.warn("Customer {} not found", newCustomerData.getName());
      return Optional.empty();
    }
    Customer customer = existingCustomer.get();
    customer.setAddress(newCustomerData.getAddress());
    customer.setPhoneNumber(newCustomerData.getPhoneNumber());
    repository.save(customer);
    return Optional.of(customer);
  }

  public Optional<Customer> update(Customer newCustomerData) {

    log.trace("Entering update() with {}", newCustomerData);
    Optional<Customer> existingCustomer = repository.read(newCustomerData.getName());
    if (!existingCustomer.isPresent()) {
      log.warn("Customer {} not found", newCustomerData.getName());
      return Optional.empty();
    }
    Customer customer = existingCustomer.get();
    if (!isNullOrEmpty(newCustomerData.getAddress())) {
      customer.setAddress(newCustomerData.getAddress());
    }
    if (!isNullOrEmpty(newCustomerData.getPhoneNumber())) {
      customer.setPhoneNumber(newCustomerData.getPhoneNumber());
    }
    repository.save(customer);
    return Optional.of(customer);
  }

  public boolean delete(String name) {

    log.trace("Entering delete() with {}", name);
    if (!repository.read(name).isPresent()) {
      log.warn("Customer {} not found", name);
      return false;
    }
    repository.delete(name);
    return true;
  }

  public List<Customer> list() {

    log.trace("Entering list()");
    return repository.readAll();
  }
}
