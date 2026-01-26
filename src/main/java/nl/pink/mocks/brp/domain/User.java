package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nl.pink.mocks.brp.validators.BSNValidation;

import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "User details")
public record User(
        @JsonProperty("bsn") @BSNValidation @Schema(description = "9-digit BSN. Must pass the MOD-11 test", example = "232262536") String bsn,
        @JsonProperty("firstName") @NotBlank @Size(max = 255, message = "firstName must not exceed 255 characters") @Schema(description = "First name", example = "John") String firstName,
        @JsonProperty("lastName") @NotBlank @Size(max = 255, message = "lastName must not exceed 255 characters") @Schema(description = "Last name", example = "Doe") String lastName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @JsonProperty("dateOfBirth") @PastOrPresent(message = "dateOfBirth must not be in the future") @Schema(description = "Date of birth in yyyy-MM-dd format", example = "1990-01-01") LocalDate dateOfBirth) {

    public User {
        Objects.requireNonNull(bsn, "bsn is required");
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(dateOfBirth, "dateOfBirth is required");
    }

}
