package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record Person(
        @JsonProperty("address") Address address,
        @JsonProperty("user") User user
) {
    public Person {
        Objects.requireNonNull(address, "address is required");
        Objects.requireNonNull(user, "user is required");
    }
}