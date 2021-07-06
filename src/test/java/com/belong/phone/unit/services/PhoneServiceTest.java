package com.belong.phone.unit.services;

import com.belong.phone.exceptions.DataNotFoundException;
import com.belong.phone.models.PhoneNumber;
import com.belong.phone.models.PhoneNumberDto;
import com.belong.phone.repository.PhoneRepo;
import com.belong.phone.services.PhoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneServiceTest {

  @InjectMocks
  private PhoneService phoneService;

  @Mock
  private PhoneRepo phoneRepo;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(phoneService, "maxPageSize", 5);
  }

  @Test
  void getPhoneNumber_whenCustomerIdIsNull_thenReturnAllPhoneNumbers() {
    PhoneNumber phoneNumber = createPhoneNumber();
    List<PhoneNumber> phoneNumbers = Arrays.asList(phoneNumber, phoneNumber);
    when(phoneRepo.findByPageSize(anyInt())).thenReturn(Optional.of(phoneNumbers));
    assertThat(phoneService.getPhoneNumber(null, null, null, "correlationId")
        .getPhoneNumberDtos().size(), is(2));
  }


  @Test
  void getPhoneNumber_whenCustomerIdPresent_thenPhoneNumbersWithCustomerId() {
    List<PhoneNumber> phoneNumbers = Arrays.asList(createPhoneNumber());

    when(phoneRepo.findByCustomerId(any())).thenReturn(Optional.of(phoneNumbers));
    List<PhoneNumberDto> phoneNumberDtos = phoneService.getPhoneNumber("customer1", null, null, "correlationId")
        .getPhoneNumberDtos();
    assertThat(phoneNumberDtos.size(), is(1));
    assertThat(phoneNumberDtos.get(0).getPhoneNo(), is("1111"));
    assertThat(phoneNumberDtos.get(0).isActive(), is(false));
    assertThat(phoneNumberDtos.get(0).getCustomerId(), is("customer1"));
  }

  @Test
  void getPhoneNumber_whenCustomerIdNotFound_returnEmptyList() {

    when(phoneRepo.findByCustomerId(any())).thenReturn(Optional.empty());
    List<PhoneNumberDto> phoneNumberDtos = phoneService.getPhoneNumber("customer1", null, null, "correlationId")
        .getPhoneNumberDtos();
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
    phoneNumber.setCreatedTime(LocalDateTime.of(2021, 7, 4, 10, 11, 0));
    phoneNumber.setId(UUID.randomUUID());
    return phoneNumber;
  }
}