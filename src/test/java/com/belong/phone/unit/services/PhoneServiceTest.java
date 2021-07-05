package com.belong.phone.unit.services;

import com.belong.phone.exceptions.DataNotFoundException;
import com.belong.phone.models.PhoneNumber;
import com.belong.phone.models.PhoneNumberDto;
import com.belong.phone.repository.PhoneRepo;
import com.belong.phone.services.PhoneService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneServiceTest {

  @InjectMocks
  private PhoneService phoneService;

  @Mock
  private PhoneRepo phoneRepo;

  @Test
  void getPhoneNumber_whenCustomerIdIsNull_thenReturnAllPhoneNumbers() {
    PhoneNumber phoneNumber = new PhoneNumber();
    List<PhoneNumber> phoneNumbers = Arrays.asList(phoneNumber, phoneNumber);
    when(phoneRepo.findAll()).thenReturn(phoneNumbers);
    assertThat(phoneService.getPhoneNumber(null, "correlationId").size(), is(2));
  }


  @Test
  void getPhoneNumber_whenCustomerIdPresent_thenPhoneNumbersWithCustomerId() {
    List<PhoneNumber> phoneNumbers = Arrays.asList(createPhoneNumber());

    when(phoneRepo.findByCustomerId(any())).thenReturn(Optional.of(phoneNumbers));
    List<PhoneNumberDto> phoneNumberDtos = phoneService.getPhoneNumber("customer1", "correlationId");
    assertThat(phoneNumberDtos.size(), is(1));
    assertThat(phoneNumberDtos.get(0).getPhoneNo(), is("1111"));
    assertThat(phoneNumberDtos.get(0).isActive(), is(false));
    assertThat(phoneNumberDtos.get(0).getCustomerId(), is("customer1"));
  }

  @Test
  void getPhoneNumber_whenCustomerIdNotFound_returnEmptyList() {

    when(phoneRepo.findByCustomerId(any())).thenReturn(Optional.empty());
    List<PhoneNumberDto> phoneNumberDtos = phoneService.getPhoneNumber("customer1", "correlationId");
    assertThat(CollectionUtils.isEmpty(phoneNumberDtos), is(true));
  }

  @Test
  void activatePhoneNumber_whenPhoneNumberIsPresent_saveInDB() {

    when(phoneRepo.findByCustomerIdAndPhoneNumber(any(), any())).thenReturn(Optional.of(createPhoneNumber()));
    phoneService.activatePhoneNumber("122", "customer1", false, "correlationId");
    verify(phoneRepo, times(1)).save(any());
  }

  @Test
  void activatePhoneNumber_whenPhoneNumbeNotFound_throwNotFoundException() {

    when(phoneRepo.findByCustomerIdAndPhoneNumber(any(), any())).thenReturn(Optional.empty());
    assertThrows(DataNotFoundException.class, () ->
        phoneService.activatePhoneNumber("122", "customer1", false, "correlationId"));
    verify(phoneRepo, times(0)).save(any());
  }

  private PhoneNumber createPhoneNumber() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setPhoneNo("1111");
    phoneNumber.setActive(false);
    phoneNumber.setCustomerId("customer1");
    return phoneNumber;
  }
}