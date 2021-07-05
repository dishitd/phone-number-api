package com.belong.phone.blackbox;

import com.belong.phone.PhoneApplication;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {PhoneApplication.class})
@Configuration
@Sql(scripts = {"/db/test_data/phone_number_setup.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/db/test_data/phone_number_cleanup.sql"}, executionPhase = AFTER_TEST_METHOD)
class PhoneApiTest {

  public static final String X_CORRELATION_ID = "X_CORRELATION_ID";

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.basePath = "/phone-number";
    RestAssured.port = 8090;
  }

  @Test
  void getPhoneNumbers_whenNoCustomerId_returnAllPhoneNumbers() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .get("v1/phones")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_OK)
        .body(containsString("0411"));
  }

  @Test
  void activatePhoneNumber_whenCustomerIdAnd_returnSpecificPhoneNumbers() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("customerId", "2")
        .get("v1/phones")

        .then()
        .log().all()
        .statusCode(HttpStatus.SC_OK)
        .body(containsString("0444"));
  }

  @Test
  void getPhoneNumbers_whenMissingCustomerId_returnBadRequest() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("number", "04111")
        .queryParam("active", false)
        .put("v1/phones/active")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(containsString("400"));
  }

  @Test
  void getPhoneNumbers_whenMissingPhoneNumber_returnBadRequest() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("customerId", "04111")
        .queryParam("active", false)
        .put("v1/phones/active")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(containsString("400"));
  }

  @Test
  void getPhoneNumbers_whenMissingActiveFlagFlag_returnBadRequest() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("customerId", "04111")
        .queryParam("number", "11")
        .put("v1/phones/active")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(containsString("400"));
  }

  @Test
  void getPhoneNumbers_whenValidRequest_returnSuccess() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("number", "0444")
        .queryParam("customerId", "2")
        .queryParam("active", false)
        .put("v1/phones/active")

        .then()
        .log().all()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  void getPhoneNumbers_whenCustomerIdAndPhoneNumberNotMatching_returnNotFound() {
    given()
        .log().all()
        .header(X_CORRELATION_ID, "1234")
        .when()
        .queryParam("number", "0444")
        .queryParam("customerId", "3")
        .queryParam("active", false)
        .put("v1/phones/active")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(containsString("404"));
  }


}
