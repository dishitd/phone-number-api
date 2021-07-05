package com.belong.phone.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "phone_number")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PhoneNumber {

  @Id
  private UUID id;
  private String customerId;
  private String phoneNo;
  private boolean isActive;

  @CreatedDate
  private LocalDateTime createdTime;
}
