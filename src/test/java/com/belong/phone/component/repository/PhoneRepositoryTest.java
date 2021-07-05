package com.belong.phone.component.repository;

import com.belong.phone.component.ComponentTest;
import com.belong.phone.models.PhoneNumber;
import com.belong.phone.repository.PhoneRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ComponentTest
@Sql(scripts = {"/db/test_data/phone_number_setup.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/db/test_data/phone_number_cleanup.sql"}, executionPhase = AFTER_TEST_METHOD)
class PhoneRepositoryTest {

  @Autowired
  private PhoneRepo phoneRepo;

  @Test
  void findByCustomerId_givenInvalidCustomerId_ReturnPhoneNumberList() {
    Optional<List<PhoneNumber>> optionalPhoneNumbers = phoneRepo.findByCustomerId("3");
    assertThat(optionalPhoneNumbers.get().size(), is(0));

  }

  @Test
  void findByCustomerId_givenCustomerId_ReturnPhoneNumberList() {
    Optional<List<PhoneNumber>> optionalPhoneNumbers = phoneRepo.findByCustomerId("1");

    assertThat(optionalPhoneNumbers.get().size(), is(3));

  }

  @Test
  void findByCustomerIdAndPhoneNumber_givenCustomerIdAndPhoneNumber_ReturnPhoneNumber() {
    Optional<PhoneNumber> phoneNumber = phoneRepo.findByCustomerIdAndPhoneNumber("2", "0444");

    assertThat(phoneNumber.get().getCustomerId(), is("2"));
    assertThat(phoneNumber.get().getPhoneNo(), is("0444"));

  }
}
