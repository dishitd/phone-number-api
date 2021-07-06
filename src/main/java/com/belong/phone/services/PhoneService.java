package com.belong.phone.services;

import com.belong.phone.exceptions.DataNotFoundException;
import com.belong.phone.models.PhoneNumber;
import com.belong.phone.models.PhoneNumberDto;
import com.belong.phone.models.PhoneNumberResponse;
import com.belong.phone.repository.PhoneRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneService {

  public static final String TOKEN_DELIMITER = "_";
  private final PhoneRepo phoneRepo;

  @Value("${services.phoneNo.pageSize}")
  private Integer maxPageSize;

  public PhoneNumberResponse getPhoneNumber(String customerId, String pageToken, Integer pageSize, String correlationId) {
    List<PhoneNumber> phoneNumbers;
    if (isNull(customerId)) {
      phoneNumbers = getPhoneNumbersByPage(pageToken, pageSize);
      return PhoneNumberResponse.builder()
          .phoneNumberDtos(convertToDtoList(phoneNumbers))
          .pageToken(generatePageToken(phoneNumbers))
          .build();
    } else {
      phoneNumbers = getPhoneNumberByCustomer(customerId, correlationId);
      return PhoneNumberResponse.builder().phoneNumberDtos(convertToDtoList(phoneNumbers)).build();
    }

  }

  private String generatePageToken(List<PhoneNumber> phoneNumbers) {
    if (!CollectionUtils.isEmpty(phoneNumbers)) {
      PhoneNumber lastPhoneNumber = phoneNumbers.get(phoneNumbers.size() - 1);
      return PhoneUtil.convertStringToHex(String.join(TOKEN_DELIMITER,
          lastPhoneNumber.getCreatedTime().format(DateTimeFormatter.ISO_DATE_TIME), lastPhoneNumber.getId().toString()));
    }
    return null;
  }

  private List<PhoneNumberDto> convertToDtoList(List<PhoneNumber> phoneNumbers) {
    return phoneNumbers.stream().parallel().map(PhoneNumberDto::mapPhoneNumber).collect(Collectors.toList());
  }

  private List<PhoneNumber> getPhoneNumberByCustomer(String customerId, String correlationId) {
    return phoneRepo.findByCustomerId(customerId).orElseGet(() -> {
      log.warn("No data found for customerId: {} with correlationId: {}", customerId, correlationId);
      return Collections.emptyList();
    });
  }

  public void activatePhoneNumber(String number, String customerId, boolean activeFlag, String correlationId) {
    PhoneNumber phoneNumber = phoneRepo.findByCustomerIdAndPhoneNumber(customerId, number).orElseThrow(() ->
        new DataNotFoundException("Matching customer and phone number not found for correlation Id: " + correlationId));
    phoneNumber.setActive(activeFlag);
    updatePhoneNumberStatus(phoneNumber);

  }

  @Transactional
  private void updatePhoneNumberStatus(PhoneNumber phoneNumber) {
    phoneRepo.save(phoneNumber);
  }

  private List<PhoneNumber> getPhoneNumbersByPage(String pageToken, Integer pageSize) {
    int size = getPageSize(pageSize);
    if (pageToken == null) {
      return phoneRepo.findByPageSize(size).orElseGet(Collections::emptyList);
    }
    String[] pageId = PhoneUtil.convertHexToString(pageToken).split(TOKEN_DELIMITER);
    LocalDateTime pageTime = LocalDateTime.parse(pageId[0]);
    return phoneRepo.findByPageSizeAndToken(pageId[1], pageTime, size)
        .orElseGet(Collections::emptyList);
  }

  private int getPageSize(Integer pageSize) {
    return isNull(pageSize) || pageSize > maxPageSize ? maxPageSize : pageSize;
  }

}
