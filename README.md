# PHONE NUMBER API

## Microservice to retrieve phone numbers and activate numbers

### API Background

We are a Telecom operator. In our database, we are starting to store phone numbers associated to customers (1 customer:
N phone numbers) and we will provide interfaces to manage them. We need to provide the below capabilities:

* get all phone numbers
* get all phone numbers of a single customer
* activate a phone number

### Challenge

* Provide interface specifications for the above functions/capabilities.
* Provide an implementation of the formulated specifications.

---

## Solution Brief

The solution is implemented using Springboot with Gradle. Solution consists of:

* get all phone numbers
* get all phone numbers of a single customer
* activate a phone number

Functionally, I have also implemented deactivating phone number API.

In addition to above, the code has below features:

* DB can be started using `./gradlew composeDown`
* Integrated DB Migration using Flyway
* Code has tests in 3 categories:
  * unit test for logic tests * component test for DB query validation
  * integration test to validate end to end API calls using RestAssured
* Integrated with Jacoco for test coverage
* Integrated with Swagger to view interface specification
* Enabled actuator for health check

## Important Gradle Tasks

| Gradle Tasks | Description | Pre-requisite |
| ------------ | ----------- | ------------- |
| `./gradlew composeUp` | Starts postgres container | |
| `./gradlew test` | Executes unit and component test | composeUp is required for componentTest |
| `./gradlew bootRun`| Starts Application|
| `./gradlew integrationTest` | Executes integration test | ./gradlew bootRun|

## Steps to get the code running

* Checkout the code from Github repository
* Go to the terminal under `/phone-number-api`
* Execute `./gradlew composeUp` to start Postgres DB
* To load sample data:
  * Go to `/phone-number-api/src/test/resources/db/test_data` and execute the `phone_number_setup.sql` script. This will
    load sample data into DB
* Execute `./gradlew bootRun`

### Interface Specification

Swagger has been integrated into the code. Please click on link after application
starts : **[Interface Specification](http://localhost:8090/phone-number/swagger-ui/#/phone-controller)**

### Application Base URL

Base URL is exposed at 8090: **[Base URL](http://localhost:8090)**

### Endpoints Exposed

| Request Type | Endpoint | Description |
  | ------------ | -------- | ----------- |
| GET | `/actuator/health` | Gets the status of the service |
| GET | `/v1/phones/` | Returns all phone numbers in the DB |
| GET | `v1/phones/?customerId=1` | Returns phone numbers associated with customerId |
| PUT | `v1/phones/active?customerId=5&number=0411&active=true` | Updates the Active flag. It can be used for both activation and deactivation |

### Assumptions

* Only activation and deactivation is implemented through API. No new phone numbers were required to be inserted
* This is an Internal facing API presumably implemented through a API or Kong Gateway
* I assumed Customer table to present already and have only used CustomerId from the Customer table assuming customer
  table is already present
* Phone number is not deleted. It is only deactivated
* As it mentioned activation, phone numbers cannot be inserted into the DB. Only, the active flag can be updated.
* Assumed that other application-sit, prod files are not required to be updated

### Enhancements

* Pagination needs to be implemented to protect the API from timing out as well as for consistent performance
* API does not validate if same number is assigned to multiple customers
* Header validation needs to be added
* Add additional test cases 
