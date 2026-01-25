package nl.pink.mocks.brp.controller;

import nl.pink.mocks.brp.domain.Address;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.domain.User;
import nl.pink.mocks.brp.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PersonService personService;

    @Test
    void testGetPerson_success() throws Exception {
        User user = new User("123456789", "Daan", "de Jong", LocalDate.of(1990, 1, 1));
        Address address = new Address("Ridderplein", "123", "1234AB", "Gemert", "NL");
        Person person = new Person(address, user);
        when(personService.getByBsn(eq("123456789"))).thenReturn(person);
        mockMvc.perform(get("/brp/person/123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value("Daan"))
                .andExpect(jsonPath("$.address.city").value("Gemert"));
    }

    @Test
    void testGetPerson_notFound() throws Exception {
        when(personService.getByBsn(eq("000000000"))).thenReturn(null);
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
        User user = new User("123456789", "Willem", "de Oud", LocalDate.of(1990, 1, 1));
        Address address = new Address("Pastoor Poellplein", "123", "1234AB", "Gemert", "NL");
        Person person = new Person(address, user);
        when(personService.getByBsn(eq("123456789"))).thenReturn(person);

        long start = System.currentTimeMillis();
        mockMvc.perform(get("/brp/person/123456789")
                        .header("X-Forced-Status", "504"))
                .andExpect(status().isGatewayTimeout());
    }

}
