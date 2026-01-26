package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Schema(description = "Person data model")
public record Person(
        @JsonProperty("address") @Schema(description = "Address details") @Valid @NotNull Address address,
        @JsonProperty("user") @Schema(description = "User details")@Valid @NotNull User user
) {
    public Person {
        Objects.requireNonNull(address, "address is required");
        Objects.requireNonNull(user, "user is required");
    }
}