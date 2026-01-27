package nl.pink.mocks.brp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.exception.PersonFileException;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import nl.pink.mocks.brp.exception.PersonValidationException;
import nl.pink.mocks.brp.utils.BsnUtils;
import nl.pink.mocks.brp.utils.FakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PersonService {

    public static final String PERSON_FILE_NAME_FORMAT = "person-%s.json";
    private final ObjectMapper objectMapper;
    private final Path baseDir;
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    Validator validator;

    public PersonService(ObjectMapper objectMapper,
                         @Value("${environment.person-folder-path}") String storageDir,
                         Validator validator) {
        this.objectMapper = objectMapper;
        this.baseDir = Paths.get(storageDir);
        this.validator = validator;
    }

    public Person getByBsn(String bsn) throws IllegalArgumentException, IOException, PersonNotFoundException {
        String personFileName = "person-%s.json".formatted(bsn);
        Path personFilePath = baseDir.resolve(personFileName);

        if (!Files.exists(personFilePath)) {
            String personNotFoundErrorMessage = "Person with BSN %s not found".formatted(bsn);
            log.debug(personNotFoundErrorMessage);
            throw new PersonNotFoundException(personNotFoundErrorMessage);
        }
        try (InputStream is = Files.newInputStream(personFilePath)) {
            return objectMapper.readValue(is, Person.class);
        } catch (IOException e) {
            log.error("Failed to read person resource: {}", personFilePath, e);
            throw new PersonFileException("Failed to read person for BSN: " + bsn, e);
        }
    }

    public void createPerson(Person person) throws PersonFileException, PersonValidationException {
        String filename = PERSON_FILE_NAME_FORMAT.formatted(person.user().bsn());
        Path target = baseDir.resolve(filename);
        try {
            Files.createDirectories(baseDir); //create directories or ignore if it already exists, avoiding FileNotFoundException
            if (Files.exists(target)) {
                throw new PersonFileException("Person file already exists: " + filename);
            }
            objectMapper.writeValue(target.toFile(), person);
            log.info("Person with BSN {} saved successfully to {}", person.user().bsn(), target);
        } catch (IOException e) {
            log.error("Failed to save person with BSN {}: {}", person.user().bsn(), e.getMessage(), e);
            throw new PersonFileException("Failed to save person data: " + person.user().bsn(), e);
        }
    }

    public void deletePerson(String bsn) throws PersonFileException {
        String personFileName = PERSON_FILE_NAME_FORMAT.formatted(bsn);
        Path personFilePath = baseDir.resolve(personFileName);
        try {
            Files.deleteIfExists(personFilePath);
            log.info("Person file for BSN {} deleted successfully", bsn);
        } catch (IOException e) {
            log.error("Failed to delete person file for BSN {}: {}", bsn, e.getMessage(), e);
            throw new RuntimeException("Failed to delete person file for BSN: " + bsn, e);
        }
    }

    public Person generateAndSaveRandomPerson() throws PersonFileException {
        Person person = FakerFactory.createRandomPerson();
        createPerson(person);
        return person;
    }

    public Person upsertPerson(String bsn, Person person) throws PersonFileException {
        if (BsnUtils.isNotValidBsn(bsn)) {
            throw new IllegalArgumentException("BSN is invalid");
        }
        if (person.user().bsn().equalsIgnoreCase(bsn)) {
            String filename = PERSON_FILE_NAME_FORMAT.formatted(bsn);
            Path target = baseDir.resolve(filename);
            try {
                Files.createDirectories(baseDir); //create directories or ignore if it already exists, avoiding FileNotFoundException
                objectMapper.writeValue(target.toFile(), person);
                log.info("Person data saved successfully");
                return person;
            } catch (IOException e) {
                log.error("Failed to save person data! ", e);
                throw new PersonFileException("Failed to save person data!", e);
            }
        } else {
            throw new IllegalArgumentException("BSN in path and person do not match");
        }
    }
}
