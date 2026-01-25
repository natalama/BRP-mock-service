package nl.pink.mocks.brp.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrpMockServiceIntegrationTest {

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void forcedStatusViaHeader() {
        restTestClient.get()
                .uri("/brp/person/123456789")
                .header("X-Forced-Status", "404")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void forcedStatusViaQueryParam() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/brp/person/123456789")
                        .queryParam("forcedMockStatus", "500")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
