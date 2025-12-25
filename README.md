# RestAssured Data-Driven API Testing

This project demonstrates data-driven API testing using RestAssured, TestNG, and Java. It includes:
- CRUD and scenario-based tests for restful-api.dev and reqres.in
- Data-driven execution from CSV and Excel files
- Logging with SLF4J and logback
- Allure reporting integration

## Project Structure
- `src/main/java/utils/` - Utility classes
- `src/test/java/tests/` - Test classes for restful-api.dev
- `src/test/java/reqres/` - Test classes and data providers for reqres.in
- `src/test/resources/` - Test data files (CSV, Excel)

## How to Run
1. Install Java 11+ and Maven
2. (Optional) Install Git for version control
3. Run tests:
   ```
   mvn test
   ```
4. Generate Allure report:
   ```
   mvn allure:serve
   ```

## Dependencies
- RestAssured
- TestNG
- Apache POI
- SLF4J & logback-classic
- Allure TestNG

## Author
Your Name# Rest Assured Data-Driven API Test Framework

This project demonstrates a data-driven API testing framework using Rest Assured, TestNG, and Allure for https://jsonplaceholder.typicode.com/.

## Features
- Request, response, schema, and security test layers
- Positive and negative scenario coverage
- Data-driven tests (CSV-based)
- Authentication handling (extendable)
- Logging and Allure reporting

## Structure
- `src/main/java/utils/`: Base test setup, data providers
- `src/test/java/tests/`: Test classes
- `src/test/resources/`: Test data (CSV)
- `pom.xml`: Maven dependencies
- `testng.xml`: TestNG suite config

## Usage
1. Install Java 11+ and Maven
2. Run tests:
   ```
   mvn clean test
   ```
3. Generate Allure report:
   ```
   mvn allure:serve
   ```

## Extend
- Add more data to `testdata.csv` for data-driven tests
- Add authentication logic in `BaseTest` if needed
- Add schema validation and security tests as required
