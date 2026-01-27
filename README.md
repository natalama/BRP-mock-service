# BRP-mock-service
Mock BRP service voor geautomatiseerde testen. De BRP (Basisregistratie Personen) levert persoonsgerelateerde gegevens.

## API-documentation
The swagger documentation is available at: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/) after starting the server.

## How to run

### Locally:

1. Make sure you have Java 25 or higher installed.
2. Clone the repository
3. Run BrpMockServiceApplication.java in either one of these ways:\
   a. In command line: ` .\mvnw spring-boot:run` or ` .\mvn spring-boot:run` if you have maven.  
   b. in your IDE, run the main class `BrpMockServiceApplication.java`\
4. The server will start on port 8080 by default.


## Configurable elements: 

You can configure the following elements using environment variables or application.properties file:

- ENV_SPRING_PROFILES_ACTIVE: Set the active Spring profile (default: "local, security-disabled")
- BRP_PERSON_FOLDER_PATH: Path to the folder containing person data (default: "data/persons")
- BRP_GLOBAL_FORCED_RESPONSE_STATUS: Default response status for all mock endpoints (default: none)
- BRP_GLOBAL_FORCED_DELAY_MS: Default response delay in milliseconds for all mock endpoints (default: 0)

## Mocked status and simulated delays

### Mocking the response status
You can mock the returned status for endpoints through one of the following: 
1. Globally: Edit directly in application.yaml file. Example: 
```
  global-forced-response-status: 400
```
this will force all endpoints to return status 400 unless overridden by one of the other methods below:
2. Setting BRP_GLOBAL_FORCED_RESPONSE_STATUS environment variable 
3. Per request by doing one of the following:
   4. Add header X-Mocked-Status with the desired status code. Example:\
         `curl --request GET --url 'http://localhost:8080/brp/person/174096151' --header 'Mocked-Status: 404' `
   5. Add query parameter mockedStatus with the desired status code. Example:\
         `curl --request POST --url 'http://localhost:8080/brp/person?mockedStatus=503' `

**NB: The status code must be valid**

### Simulating delays

You can simulate response delays for endpoints through one of the following:
1. Globally: Edit directly in application.yaml file. Example:
```
  global-forced-delay-ms: 2000
```
this will force all endpoints to delay their response by 2000 ms unless overridden by one of the other methods below:
2. Setting BRP_GLOBAL_FORCED_DELAY_MS environment variable
3. Per request by doing one of the following:
   4. Add header X-Mocked-Delay with the desired delay in milliseconds. Example:\
         `curl --request GET --url 'http://localhost:8080/brp/person/174096151' --header 'Forced-Delay: 1500' `
   5. Add query parameter mockedDelay with the desired delay in milliseconds. Example:\
         `curl --request POST --url 'http://localhost:8080/brp/person?forcedDelayMs=3000'`


### Order of precedence
When both global and per-request configurations are set, the following order of precedence applies (from highest to lowest):
1. Per-request query parameter (mockedStatus or mockedDelay)
2. Per-request header (X-Mocked-Status or X-Mocked-Delay)
3. Global configuration (BRP_GLOBAL_FORCED_RESPONSE_STATUS or BRP_GLOBAL_FORCED_DELAY

Want to disregard the global-forced-response-status for a specific request? Set the header or query parameter to "-" (without "").

### Known limitations

- Scenario or scenario-switching via header is not supported yet
- Not dockerized yet
- There's no correct implementation of authentication and authorization yet
- No internationalization (i18n) yet