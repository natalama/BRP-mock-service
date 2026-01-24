package nl.pink.mocks.brp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.pink.mocks.brp.domain.Person;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public PersonService(ObjectMapper objectMapper,
                         @Value("${environment.person-folder-path}") String storageDir) {
        this.objectMapper = objectMapper;
        this.baseDir = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    public Person getByBsn(String bsn) throws IllegalArgumentException, IOException {
        if (StringUtils.isBlank(bsn)) {
            log.warn("Requested BSN is blank");
            throw new IllegalArgumentException("BSN is blank");
        }
        String personFileName = "person-%s.json".formatted(bsn);
        Path personFilePath = baseDir.resolve(personFileName);

        if (!Files.exists(personFilePath)) {
            log.info("Person with BSN {} not found", bsn);
            return null;
        }
        try (InputStream is = Files.newInputStream(personFilePath)) {
            return objectMapper.readValue(is, Person.class);
        } catch (IOException e) {
            log.error("Failed to read person resource: {}", personFilePath, e);
            throw new IOException("Failed to read person for BSN: " + bsn, e);
        }
    }

    public void savePerson(Person person) {
        String bsn = person.user().bsn();
        if (StringUtils.isBlank(bsn)) {
            throw new IllegalArgumentException("BSN is blank");
        }
        try {
            Files.createDirectories(baseDir); // safe if directory already exists
        } catch (IOException e) {
            log.error("Failed to create directory `personFiles`", e);
            throw new RuntimeException("Failed to create storage directory", e);
        }

        String filename = PERSON_FILE_NAME_FORMAT.formatted(bsn);
        Path target = baseDir.resolve(filename);

        if (Files.exists(target)) {
            throw new IllegalArgumentException("Person file already exists: " + target.toString());
        }

        try {
            objectMapper.writeValue(target.toFile(), person);
            log.info("Person with BSN {} saved successfully to {}", bsn, target);
        } catch (IOException e) {
            log.error("Failed to save person with BSN {}: {}", bsn, e.getMessage(), e);
            throw new RuntimeException("Failed to save person: " + bsn, e);
        }
    }

    public void deletePerson(String bsn) {
        if (StringUtils.isBlank(bsn)) {
            throw new IllegalArgumentException("BSN is blank");
        }
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

    public void upsertPerson(String bsn, Person person) {
        if (StringUtils.isBlank(bsn)) {
            throw new IllegalArgumentException("BSN is blank");
        }
        if (person.user().bsn().equalsIgnoreCase(bsn)) {
            String filename = PERSON_FILE_NAME_FORMAT.formatted(bsn);
            Path target = baseDir.resolve(filename);
            try {
                objectMapper.writeValue(target.toFile(), person);
                log.info("Person with BSN {} saved successfully to {}", bsn, target);
            } catch (IOException e) {
                log.error("Failed to save person with BSN {}: {}", bsn, e.getMessage(), e);
                throw new RuntimeException("Failed to save person: " + bsn, e);
            }
        } else {
            throw new IllegalArgumentException("BSN in path and person do not match");
        }
    }
}
