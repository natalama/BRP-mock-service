package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Person data model")
public record Person(
        @JsonProperty("address") @Schema(description = "Address details") Address address,
        @JsonProperty("user") @Schema(description = "User details") User user
) {
    public Person {
        Objects.requireNonNull(address, "address is required");
        Objects.requireNonNull(user, "user is required");
    }
}