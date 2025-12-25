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

## Jenkins CI/CD Integration

### Maven Command Examples
- **Standard TestNG run:**
  ```sh
  mvn test -Denv=qa -Dgroups=smoke
  ```
- **Cucumber run:**
  ```sh
  mvn test -Denv=qa -Dcucumber.options="--tags @smoke"
  ```
- **Parallel execution:**
  Configure in `pom.xml`:
  ```xml
  <parallel>methods</parallel>
  <threadCount>4</threadCount>
  ```
  Or via CLI:
  ```sh
  mvn test -Denv=qa -Dgroups=smoke -Dsurefire.threadCount=4 -Dsurefire.parallel=methods
  ```

### Jenkins Job Configuration Steps
1. **Create Pipeline Job**
   - Job type: Pipeline
   - Pipeline script from SCM: point to your repo and Jenkinsfile
2. **Parameters**
   - Add `ENV`, `TAGS`, `BRANCH` as parameters
3. **Credentials**
   - Store secrets (API keys, tokens) in Jenkins Credentials
   - Reference via `withCredentials` or `environment` block
4. **Allure Plugin**
   - Install Allure Jenkins plugin for report publishing
5. **Notifications**
   - Configure Email/Slack/MS Teams plugins
   - Add notification steps in `post` block

### Jenkins Build Parameters

When configuring your Jenkins Pipeline job, add the following build parameters for flexible execution:

- **ENV** (Choice Parameter):
  - Description: Target environment for test execution
  - Choices: `dev`, `qa`, `stage`

- **TAGS** (String Parameter):
  - Description: TestNG groups or Cucumber tags (comma-separated)
  - Example: `smoke,regression` or `@api,@smoke`

- **BRANCH** (String Parameter):
  - Description: Git branch to build
  - Default: `main`
  - Example: `main`, `develop`, `release/1.0`

These parameters are referenced in the Jenkinsfile and allow you to control which environment, test groups/tags, and branch are used for each build.

**How to add in Jenkins UI:**
1. Go to your Pipeline job → Configure.
2. Under "This project is parameterized", click "Add Parameter" and select the appropriate type (Choice or String).
3. Fill in the name, description, and values as above.
4. Save the job.

### Best Practices & Common Pitfalls
- Parameterize environment/branch/tags for flexibility
- Externalize configs (e.g., `src/test/resources/application.properties`) and load by `-Denv`
- Never hardcode secrets; use Jenkins credentials
- Use Surefire/TestNG parallel settings for scalability
- Always archive logs, reports, and screenshots
- Mark build UNSTABLE for test failures, FAILURE for infra/compilation issues
- Integrate with Slack/Email/MS Teams for build status
- Mask sensitive data using Jenkins plugins
- Use `cleanWs()` to avoid disk bloat
- Set reasonable timeouts to avoid hanging builds
- Use `buildDiscarder` to manage disk usage

---

For more details, see the Jenkinsfile in the project root.
