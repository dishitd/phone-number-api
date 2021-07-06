package com.belong.phone.controllers;


import com.belong.phone.models.PhoneNumberResponse;
import com.belong.phone.services.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.concurrent.Callable;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PhoneController {

  public static final String X_CORRELATION_ID = "X_CORRELATION_ID";
  private final PhoneService phoneService;

  @GetMapping("v1/phones")
  public Callable<ResponseEntity<PhoneNumberResponse>> getPhoneNumbers(
      @RequestHeader final HttpHeaders requestHeaders,
      @RequestParam(name = "customerId", required = false) String customerId,
      @RequestParam(name = "pageToken", required = false) String pageToken,
      @RequestParam(name = "pageSize", required = false,
          defaultValue = "${services.phoneNo.pageSize}") @Min(2) int pageSize

  ) {

    String correlationId = requestHeaders.getFirst(X_CORRELATION_ID);
    log.debug("Request received. correlationId: {}, customerId: {}", correlationId, customerId);
    return () -> new ResponseEntity<>(phoneService.getPhoneNumber(customerId, pageToken, pageSize, correlationId),
        HttpStatus.OK);
  }

  @PutMapping("v1/phones/active")
  public Callable<ResponseEntity> activatePhoneNumber(
      @RequestHeader final HttpHeaders requestHeaders,
      @RequestParam(name = "number") @NotBlank String number,
      @RequestParam(value = "customerId") @NotBlank String customerId,
      @RequestParam(value = "active") @NotBlank boolean activeFlag
  ) {
    String correlationId = requestHeaders.getFirst(X_CORRELATION_ID);
    phoneService.activatePhoneNumber(number, customerId, activeFlag, correlationId);
    return () -> ResponseEntity.noContent().header(X_CORRELATION_ID, correlationId).build();

  }


}

