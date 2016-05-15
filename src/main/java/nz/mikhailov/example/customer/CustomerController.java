package nz.mikhailov.example.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1")
public class CustomerController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private CustomerService service;

  @RequestMapping(path = "/customer", method = RequestMethod.GET)
  public ResponseEntity<List<Customer>> list() {

    log.trace("Entering list()");
    List<Customer> customers = service.list();
    if (customers.isEmpty()) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    return new ResponseEntity<>(customers, OK);
  }

  @RequestMapping(path = "/customer/{name}", method = RequestMethod.GET)
  public ResponseEntity<Customer> read(@PathVariable String name) {

    log.trace("Entering read() with {}", name);
    return service.read(name)
        .map(customer -> new ResponseEntity<>(customer, OK))
        .orElse(new ResponseEntity<>(NOT_FOUND));
  }

  @RequestMapping(path = "/customer", method = RequestMethod.POST)
  public ResponseEntity<Customer> create(@RequestBody @Valid Customer customer) {

    log.trace("Entering create() with {}", customer);
    return service.create(customer)
        .map(newCustomerData -> new ResponseEntity<>(newCustomerData, CREATED))
        .orElse(new ResponseEntity<>(CONFLICT));
  }

  @RequestMapping(path = "/customer/{name}", method = RequestMethod.PUT)
  public ResponseEntity<Customer> put(@PathVariable String name, @RequestBody Customer customer) {

    log.trace("Entering put() with {}, {}", name, customer);
    return service.replace(customer.withName(name))
        .map(newCustomerData -> new ResponseEntity<>(newCustomerData, OK))
        .orElse(new ResponseEntity<>(NOT_FOUND));
  }

  @RequestMapping(path = "/customer/{name}", method = RequestMethod.PATCH)
  public ResponseEntity<Customer> patch(@PathVariable String name, @RequestBody Customer customer) {

    log.trace("Entering patch() with {}, {}", name, customer);
    return service.update(customer.withName(name))
        .map(newCustomerData -> new ResponseEntity<>(newCustomerData, OK))
        .orElse(new ResponseEntity<>(NOT_FOUND));
  }

  @RequestMapping(path = "/customer/{name}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String name) {

    log.trace("Entering delete() with {}", name);
    return service.delete(name) ?
        new ResponseEntity<>(NO_CONTENT) :
        new ResponseEntity<>(NOT_FOUND);
  }
}
