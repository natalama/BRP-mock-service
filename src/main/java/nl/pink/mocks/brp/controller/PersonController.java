package nl.pink.mocks.brp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.exception.PersonNotFoundException;
import nl.pink.mocks.brp.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Person", description = "BRP Person mock endpoints")
@RestController
@RequestMapping("/brp/person")
public class PersonController {

    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(summary = "Get person by BSN", description = "Returns a person for the given BSN if found")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "400", description = "Invalid BSN supplied")
    })
    @GetMapping("/{bsn}")
    public ResponseEntity<Person> getPersonByBsn(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
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
    public ResponseEntity<Object> createPerson(
            @RequestHeader(value = "X-Forced-Status", required = false) String forcedStatus,
            @RequestHeader(value = "X-Forced-Delay", required = false) String forcedDelay,
            @RequestParam(value = "forcedMockStatus", required = false) String forcedMockStatus,
            @Parameter(description = "Person object to create", required = true)
            @RequestBody Person person) {
        personService.savePerson(person);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Insert or update a person", description = "Updates an existing person or creates a new one if not found")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
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


    @Operation(summary = "Delete person by BSN", description = "Deletes the person record for the given BSN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "400", description = "Invalid BSN supplied")
    })
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