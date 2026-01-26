package nl.pink.mocks.brp.controller;

import nl.pink.mocks.brp.constants.RequestConstants;
import nl.pink.mocks.brp.domain.Address;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.domain.User;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import nl.pink.mocks.brp.service.PersonService;
import nl.pink.mocks.brp.utils.FakerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PersonController.class)
//disable security for this test
@AutoConfigureMockMvc(addFilters = false)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PersonService personService;

    @Test
    void testGetPerson_success() throws Exception {
        Person person = FakerFactory.createRandomPerson();
        String bsn = person.user().bsn();
        when(personService.getByBsn(eq(bsn))).thenReturn(person);
        mockMvc.perform(get("/brp/person/%s".formatted(bsn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value(person.user().firstName()))
                .andExpect(jsonPath("$.address.city").value(person.address().city()));
    }

    @Test
    void testGetPerson_notFound() throws Exception {
        when(personService.getByBsn(eq("000000000"))).thenThrow(PersonNotFoundException.class);
        mockMvc.perform(get("/brp/person/000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePerson_success() throws Exception {
        String json = """
                {
                "address": {
                    "street": "Sesame Street",
                    "houseNumber": "123",
                    "postalCode": "1234AB",
                    "city": "Amsterdam",
                    "country": "NL"
                },
                "user":{
                 "bsn": "123456789",
                 "firstName": "John",
                 "lastName": "Doe",
                 "dateOfBirth": "1990-01-01"}
                }""";
        mockMvc.perform(post("/brp/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetPerson_withMockedStatus() throws Exception {
        Person person = FakerFactory.createRandomPerson();

        when(personService.getByBsn(eq("635926652"))).thenReturn(person);
        long start = System.currentTimeMillis();
        mockMvc.perform(get("/brp/person/635926652")
                        .header(RequestConstants.HEADER_X_MOCKED_STATUS, "504"))
                .andExpect(status().isGatewayTimeout());
    }

}
