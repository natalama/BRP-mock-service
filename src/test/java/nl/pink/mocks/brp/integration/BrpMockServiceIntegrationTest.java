package nl.pink.mocks.brp.integration;

import nl.pink.mocks.brp.constants.RequestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrpMockServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(BrpMockServiceIntegrationTest.class);
    @TempDir
    private static Path tempDir;
    private RestTestClient restTestClient;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();

    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) throws Exception {
        registry.add("environment.person-folder-path", () -> tempDir.toString());
    }

    @AfterEach
    void cleanup(){

    }

    @Test
    void testCreateFileThenGetFile() throws Exception {
        String json = """
                {
                  "address": {
                    "street": "Olthoffpark",
                    "houseNumber": "313 III",
                    "postalCode": "3631 EK",
                    "city": "Oud Jeldameer",
                    "country": "BAM"
                  },
                  "user": {
                    "bsn": "174096151",
                    "firstName": "Woes",
                    "lastName": "Laats",
                    "dateOfBirth": "2000-11-25"
                  }
                }
                """;

        restTestClient.post()
                .uri("/brp/person")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange()
                .expectStatus().isCreated();

        Path stored = tempDir.resolve("person-174096151.json");
        assertTrue(Files.exists(stored));

        restTestClient.get()
                .uri("/brp/person/174096151")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user.bsn").isEqualTo("174096151");
    }

    @Test
    void forcedStatusViaHeader() {
        restTestClient.get()
                .uri("/brp/person/013141627")
                .header(RequestConstants.HEADER_X_MOCKED_STATUS, Integer.toString(HttpStatus.NOT_FOUND.value()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void forcedStatusViaQueryParam() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/brp/person/635926652")
                        .queryParam(RequestConstants.REQ_PARAM_MOCK_STATUS, Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
