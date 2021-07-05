package com.belong.phone.services;

import com.belong.phone.exceptions.DataNotFoundException;
import com.belong.phone.models.PhoneNumber;
import com.belong.phone.repository.PhoneRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneService {

    private final PhoneRepo phoneRepo;

    public List<PhoneNumber> getPhoneNumber(String customerId, String correlationId) {
        return Objects.isNull(customerId) ? phoneRepo.findAll() : getPhoneNumberByCustomer(customerId, correlationId);
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
}
