package com.belong.phone.repository;

import com.belong.phone.models.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepo extends JpaRepository<PhoneNumber, String> {

    @Query(value = "SELECT * FROM phone_number WHERE customer_id = ?", nativeQuery = true)
    Optional<List<PhoneNumber>> findByCustomerId(String customerId);

    @Query(value = "SELECT * FROM phone_number WHERE customer_id = ? AND phone_no= ?", nativeQuery = true)
    Optional<PhoneNumber> findByCustomerIdAndPhoneNumber(String customerId, String phoneNumber);
}
