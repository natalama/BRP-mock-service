package nl.pink.mocks.brp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.pink.mocks.brp.config.JacksonConfig;
import nl.pink.mocks.brp.domain.Address;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.domain.User;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {
    private PersonService personService;
    private ObjectMapper objectMapper;
    private Path storageDir;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        objectMapper = new JacksonConfig().objectMapper();
        storageDir = tempDir;
        personService = new PersonService(objectMapper, storageDir.toString());
    }

    @Test
    void testSaveAndGetPerson_success() throws IOException, PersonNotFoundException {
        User user = new User("123456789", "John", "Doe", LocalDate.of(1990, 1, 1));
        Address address = new Address("Main Street", "123", "1234AB", "Amsterdam", "Netherlands");
        Person person = new Person(address, user);
        personService.savePerson(person);
        Person loaded = personService.getByBsn("123456789");
        assertNotNull(loaded);
        assertEquals("John", loaded.user().firstName());
        assertEquals("Amsterdam", loaded.address().city());
    }

    @Test
    void testGetPerson_notFound() throws IOException, PersonNotFoundException {
        Person loaded = personService.getByBsn("000000000");
        assertNull(loaded);
    }

    @Test
    void testSavePerson_blankBsn() {
        User user = new User("", "John", "Doe", LocalDate.of(1990, 1, 1));
        Address address = new Address("Main Street", "123", "1234AB", "Amsterdam", "Netherlands");
        Person person = new Person(address, user);
        assertThrows(IllegalArgumentException.class, () -> personService.savePerson(person));
    }

    @Test
    void testGetPerson_blankBsn() {
        assertThrows(IllegalArgumentException.class, () -> personService.getByBsn(" "));
    }
}
