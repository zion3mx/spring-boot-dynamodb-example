package nz.mikhailov.example.customer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@DynamoDBTable(tableName = "Customer")
public class Customer {

  private String name;
  private String address;
  private String phoneNumber;

  @DynamoDBHashKey(attributeName = "Name")
  @NotNull(message = "Name must not be empty")
  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public Customer withName(String name) {

    setName(name);
    return this;
  }

  @DynamoDBAttribute(attributeName = "Address")
  public String getAddress() {

    return address;
  }

  public void setAddress(String address) {

    this.address = address;
  }

  public Customer withAddress(String address) {

    setAddress(address);
    return this;
  }

  @DynamoDBAttribute(attributeName = "PhoneNumber")
  public String getPhoneNumber() {

    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {

    this.phoneNumber = phoneNumber;
  }

  public Customer withPhoneNumber(String phoneNumber) {

    setPhoneNumber(phoneNumber);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return Objects.equals(getName(), customer.getName()) &&
        Objects.equals(getAddress(), customer.getAddress()) &&
        Objects.equals(getPhoneNumber(), customer.getPhoneNumber());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getAddress(), getPhoneNumber());
  }
}
