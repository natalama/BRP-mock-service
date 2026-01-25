package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "User details")
public record User(
        @JsonProperty("bsn") @Schema(description = "9-digit BSN. Must pass the MOD-11 test", example = "232262536") String bsn,
        @JsonProperty("firstName") @Schema(description = "First name", example = "John") String firstName,
        @JsonProperty("lastName") @Schema(description = "Last name", example = "Doe") String lastName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @JsonProperty("dateOfBirth") @Schema(description = "Date of birth in yyyy-MM-dd format", example = "1990-01-01") LocalDate dateOfBirth) {

    public User {
        Objects.requireNonNull(bsn, "bsn is required");
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(dateOfBirth, "dateOfBirth is required");
    }

}
