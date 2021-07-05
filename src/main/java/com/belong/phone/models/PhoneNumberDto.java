package com.belong.phone.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PhoneNumberDto {

  private String customerId;
  private String phoneNo;
  private boolean isActive;

  public static PhoneNumberDto mapPhoneNumber(PhoneNumber phoneNumber) {
    return PhoneNumberDto.builder()
        .customerId(phoneNumber.getCustomerId())
        .phoneNo(phoneNumber.getPhoneNo())
        .isActive(phoneNumber.isActive())
        .build();
  }
}
