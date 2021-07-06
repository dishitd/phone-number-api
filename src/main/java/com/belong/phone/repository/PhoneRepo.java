package com.belong.phone.repository;

import com.belong.phone.models.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepo extends JpaRepository<PhoneNumber, String> {

  String PAGINATION_QUERY = "SELECT * FROM phone_number WHERE (created_time < ? OR (created_time = ? and id > ?) ) "
      + "AND created_time < now() ORDER BY created_time desc, id asc limit ?";

  @Query(value = "SELECT * FROM phone_number WHERE customer_id = ?", nativeQuery = true)
  Optional<List<PhoneNumber>> findByCustomerId(String customerId);

  @Query(value = "SELECT * FROM phone_number WHERE customer_id = ? AND phone_no = ?", nativeQuery = true)
  Optional<PhoneNumber> findByCustomerIdAndPhoneNumber(String customerId, String phoneNumber);

  @Query(value = PAGINATION_QUERY, nativeQuery = true)
  Optional<List<PhoneNumber>> findByPageSizeAndToken(String id, LocalDateTime createdTime, int pageSize);

  @Query(value = "SELECT * FROM phone_number ORDER BY created_time desc, id asc limit ?", nativeQuery = true)
  Optional<List<PhoneNumber>> findByPageSize(int pageSize);
}
