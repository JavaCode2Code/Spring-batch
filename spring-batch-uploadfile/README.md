# Spring Batch CSV Upload Application

A Spring Boot application that uploads a CSV file through a REST API and imports customer records into a MySQL database using Spring Batch.

## Technology Stack

- Java 17
- Spring Boot 4.1.2
- Spring Batch
- Spring Data JPA
- Spring Web MVC
- MySQL
- H2 (test database)
- Gradle
- Lombok

## Project Structure

```text
spring-batch-uploadfile/
├── build.gradle
├── settings.gradle
├── README.md
├── src/
│   ├── main/
│   │   ├── java/org/example/springbatch001/
│   │   │   ├── SpringBatch_PartitioningApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CustomerProcessor.java
│   │   │   │   ├── ExceptionSkipPolicy.java
│   │   │   │   ├── JobCompletionNotificationListener.java
│   │   │   │   ├── SpringBatchConfig.java
│   │   │   │   └── listner/StepSkipListener.java
│   │   │   ├── controller/JobController.java
│   │   │   ├── entity/Customer.java
│   │   │   └── repository/CustomerRepository.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── customers.csv
│   └── test/
│       ├── java/
│       └── resources/application.properties
└── gradlew.bat
```

## Prerequisites

- JDK 17
- MySQL Server
- Gradle Wrapper (included)
- Port `9292` available

## MySQL Setup

Create the database:

```sql
CREATE DATABASE batchdb;
```

Update these values in `src/main/resources/application.properties` if your MySQL configuration differs:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/batchdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=admin
```

Spring Batch creates its metadata tables automatically when the application starts:

```properties
spring.batch.jdbc.initialize-schema=ALWAYS
spring.batch.job.enabled=false
```

## Run the Application

Windows:

```powershell
.\gradlew.bat bootRun
```

The application starts at:

```text
http://localhost:9292
```

## CSV Testing Data

The CSV file must contain this header:

```csv
id,firstName,lastName,email,gender,contactNo,country,dob,age
```

Example:

```csv
id,firstName,lastName,email,gender,contactNo,country,dob,age
1001,Truda,Cahan,truda@example.com,Female,506-108-7773,China,01/05/2001,25
1002,Janey,Schoffel,janey@example.com,Female,716-626-1084,Japan,03/31/2016,22
1003,Quincy,Hansana,quincy@example.com,Male,853-785-2314,Portugal,09/28/2005,21
```

A larger sample file is available at:

```text
src/main/resources/customers.csv
```

Save the testing data above as `customers-test.csv` before uploading it.

## API Usage

### Upload and Import CSV

**Endpoint:** `POST /jobs/importData`

**Content-Type:** `multipart/form-data`

**Form field:** `file`

PowerShell example:

```powershell
curl.exe -X POST `
  -F "file=@C:\temp\customers-test.csv" `
  http://localhost:9292/jobs/importData

```

Do not set `Content-Type: multipart/form-data` manually. Let `curl.exe`, Postman, or the HTTP client generate the required `boundary` value.

## Troubleshooting: No Multipart Boundary

If you see:

```text
Failed to parse multipart servlet request
request was rejected because no multipart boundary was found
```

The server received data that was not a valid multipart request. The `Content-Type` header was missing or did not include a boundary.

Expected header format:

```http
Content-Type: multipart/form-data; boundary=<generated-boundary>
```

### Postman

1. Select `POST` and enter `http://localhost:9292/jobs/importData`.
2. Open the **Body** tab.
3. Select **form-data**.
4. Set the key to `file` and change its type from **Text** to **File**.
5. Select a `.csv` file.
6. Do not add a manual `Content-Type` header; Postman generates it automatically.

### Swagger or a JavaScript Client

Use `FormData` and let the browser set the boundary:

```javascript
const formData = new FormData();
formData.append("file", csvFile, "customers-test.csv");

await fetch("http://localhost:9292/jobs/importData", {
  method: "POST",
  body: formData
});
```

Do not submit raw CSV text with `application/json`, and do not add `Content-Type: multipart/form-data` yourself in JavaScript.

Successful responses contain Spring Batch job execution information, including the execution ID and status.

### Verify Imported Data

Query MySQL:

```sql
USE batchdb;
SELECT * FROM CUSTOMERS_INFO;
```

## CSV Processing Behavior

- The first CSV row is skipped as the header.
- Records are processed in chunks of 5.
- Processing filters out records with a null or empty `country` value.
- Read or processing skip failures are handled by the configured skip policy.
- The temporary uploaded file is deleted after the job starts.

## Test the Project

The test configuration uses an in-memory H2 database instead of MySQL.

Windows:

```powershell
.\gradlew.bat clean test
```

Linux/macOS:

```bash
./gradlew clean test
```

Test reports:

```text
build/reports/tests/test/index.html
```

## Build the Application

Windows:

```powershell
.\gradlew.bat clean bootJar
```

The executable JAR is created in:

```text
build/libs/
```

## Swagger API Documentation

After starting the application, open:

```text
http://localhost:9292/swagger-ui.html
```

The raw OpenAPI document is available at:

```text
http://localhost:9292/v3/api-docs
```

In Swagger UI, expand **Batch Jobs**, select `POST /jobs/importData`, choose **Try it out**, select a CSV file for `file`, and execute the request.
