package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;


public record User(@JsonProperty("bsn") String bsn,
                   @JsonProperty("firstName") String firstName,
                   @JsonProperty("lastName") String lastName,
                   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @JsonProperty("dateOfBirth") LocalDate dateOfBirth) {

    public User {
        Objects.requireNonNull(bsn, "bsn is required");
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(dateOfBirth, "dateOfBirth is required");
    }

}
