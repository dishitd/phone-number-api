package com.belong.phone.controllers;


import com.belong.phone.models.PhoneNumber;
import com.belong.phone.services.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Callable;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PhoneController {

    public static final String X_CORRELATION_ID = "X_CORRELATION_ID";
    private final PhoneService phoneService;

    @GetMapping("v1/phones")
    public Callable<ResponseEntity<List<PhoneNumber>>> getPhoneNumbers(@RequestHeader final HttpHeaders requestHeaders,
                                                                       @RequestParam(name = "customerId", required = false) String customerId) {

        String correlationId = requestHeaders.getFirst(X_CORRELATION_ID);
        log.debug("Request received. correlationId: {}, customerId: {}", correlationId, customerId);
        return () -> new ResponseEntity<>(phoneService.getPhoneNumber(customerId, correlationId), HttpStatus.OK);
    }

    @PutMapping("v1/phones/active")
    public Callable<ResponseEntity> activatePhoneNumber(@RequestHeader final HttpHeaders requestHeaders,
                                                        @RequestParam(name = "number") String number,
                                                        @RequestParam(value = "customerId") String customerId,
                                                        @RequestParam(value = "active") boolean activeFlag) {
        String correlationId = requestHeaders.getFirst(X_CORRELATION_ID);
        phoneService.activatePhoneNumber(number, customerId, activeFlag, correlationId);
        return () -> ResponseEntity.noContent().header(X_CORRELATION_ID, correlationId).build();

    }


}

