package nl.pink.mocks.brp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.exception.PersonFileException;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import nl.pink.mocks.brp.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static nl.pink.mocks.brp.constants.RequestConstants.*;

@Tag(name = "Person", description = "BRP Person mock endpoints")
@RestController
@RequestMapping("/brp/person")
public class PersonController {

    private PersonService personService;
    private final SmartValidator validator;

    public PersonController(PersonService personService, SmartValidator validator) {
        this.personService = personService;
        this.validator = validator;
    }

    @Operation(summary = "Get person by BSN", description = "Returns a person for the given BSN if found")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "400", description = "Invalid BSN supplied")
    })
    @GetMapping("/{bsn}")
    public ResponseEntity<Person> getPersonByBsn(
            @RequestHeader(value = HEADER_MOCKED_STATUS, required = false) String forcedStatus,
            @RequestHeader(value = HEADER_FORCED_DELAY_MS, required = false) String forcedDelay,
            @RequestParam(value = REQ_PARAM_MOCKED_STATUS, required = false) String forcedMockStatus,
            @RequestParam(value = REQ_PARAM_FORCED_DELAY_MS, required = false) String forcedDelayMs,
            @Parameter(description = "BSN of the person", required = true, example = "123456789")
            @PathVariable String bsn) throws PersonNotFoundException, IOException {
        Person person = personService.getByBsn(bsn);
        return ResponseEntity.ok(person);
    }

    @Operation(summary = "Create a new person", description = "Creates a new person record with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createPerson(
            @RequestHeader(value = HEADER_MOCKED_STATUS, required = false) String forcedStatus,
            @RequestHeader(value = HEADER_FORCED_DELAY_MS, required = false) String forcedDelay,
            @RequestParam(value = REQ_PARAM_MOCKED_STATUS, required = false) String forcedMockStatus,
            @RequestParam(value = REQ_PARAM_FORCED_DELAY_MS, required = false) String forcedDelayMs,
            @Parameter(description = "Person object to create", required = true)
            @Valid @RequestBody Person person) throws PersonFileException {
        Errors errors = validator.validateObject(person);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        } else {
            personService.createPerson(person);
            return ResponseEntity.status(201).body(person);
        }
    }

    @Operation(summary = "Create a new random person", description = "Generates and creates a new person record with random details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Random person created successfully")
    })
    @PostMapping(value = "/random", produces = "application/json")
    public ResponseEntity<Person> createRandomPerson(
            @RequestHeader(value = HEADER_MOCKED_STATUS, required = false) String forcedStatus,
            @RequestHeader(value = HEADER_FORCED_DELAY_MS, required = false) String forcedDelay,
            @RequestParam(value = REQ_PARAM_MOCKED_STATUS, required = false) String forcedMockStatus,
            @RequestParam(value = REQ_PARAM_FORCED_DELAY_MS, required = false) String forcedDelayMs) throws PersonFileException {
        Person person = personService.generateAndSaveRandomPerson();
        return ResponseEntity.status(201).body(person);
    }

    @Operation(summary = "Insert or update a person", description = "Updates an existing person or creates a new one if not found")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping(value = "/{bsn}", consumes = "application/json")
    public ResponseEntity<Object> upsertPerson(
            @RequestHeader(value = HEADER_MOCKED_STATUS, required = false) String forcedStatus,
            @RequestHeader(value = HEADER_FORCED_DELAY_MS, required = false) String forcedDelay,
            @RequestParam(value = REQ_PARAM_MOCKED_STATUS, required = false) String forcedMockStatus,
            @RequestParam(value = REQ_PARAM_FORCED_DELAY_MS, required = false) String forcedDelayMs,
            @PathVariable String bsn,
            @Valid @RequestBody Person person) throws PersonFileException {
        Person updatedPerson = personService.upsertPerson(bsn, person);
        return ResponseEntity.ok(updatedPerson);
    }


    @Operation(summary = "Delete person by BSN", description = "Deletes the person record for the given BSN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "400", description = "Invalid BSN supplied")
    })
    @DeleteMapping("/{bsn}")
    public ResponseEntity<Object> deletePerson(
            @RequestHeader(value = HEADER_MOCKED_STATUS, required = false) String forcedStatus,
            @RequestHeader(value = HEADER_FORCED_DELAY_MS, required = false) String forcedDelay,
            @RequestParam(value = REQ_PARAM_MOCKED_STATUS, required = false) String forcedMockStatus,
            @RequestParam(value = REQ_PARAM_FORCED_DELAY_MS, required = false) String forcedDelayMs,
            @PathVariable String bsn) throws PersonFileException {
        personService.deletePerson(bsn);
        return ResponseEntity.ok("Person with bsn %s deleted".formatted("*****" + bsn.substring(6)));
    }
}