package nl.pink.mocks.brp.controller;

import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/brp/person")
public class PersonController {

    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{bsn}")
    public ResponseEntity<Person> getPersonByBsn(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
            @PathVariable String bsn) {
        try {
            Person person = personService.getByBsn(bsn);
            if (person == null) {
                ;
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(person);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createPerson(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
            @RequestBody Person person) {
        personService.savePerson(person);
        return ResponseEntity.status(201).build();
    }

    @PutMapping(value = "/{bsn}", consumes = "application/json")
    public ResponseEntity<Object> upsertPerson(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
            @PathVariable String bsn,
            @RequestBody Person person) {
        personService.upsertPerson(bsn, person);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{bsn}")
    public ResponseEntity<Object> deletePerson(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
            @PathVariable String bsn) {
        personService.deletePerson(bsn);
        return ResponseEntity.ok().build();
    }
}