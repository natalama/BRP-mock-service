package nl.pink.mocks.brp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import net.datafaker.Faker;
import nl.pink.mocks.brp.config.JacksonConfig;
import nl.pink.mocks.brp.domain.Address;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.domain.User;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import nl.pink.mocks.brp.utils.FakerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {
    private PersonService personService;
    private ObjectMapper objectMapper;
    @TempDir
    private Path tempDir;
    private Faker faker;

    SmartValidator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new JacksonConfig().objectMapper();;
        personService = new PersonService(objectMapper, tempDir.toString(), validator);
        faker = new Faker();
    }

    @Test
    void testSaveAndGetPerson_success() throws IOException, PersonNotFoundException {
        Person randomPerson = FakerFactory.createRandomPerson();
        personService.createPerson(randomPerson);
        Person loaded = personService.getByBsn(randomPerson.user().bsn());
        assertNotNull(loaded);
        assertEquals(randomPerson.user().firstName(), loaded.user().firstName());
        assertEquals(randomPerson.address().city(), loaded.address().city());
    }

    @Test
    void testGetPerson_notFound() throws IOException {
        assertThrows(PersonNotFoundException.class, () -> personService.getByBsn("000000000"));
    }
}
