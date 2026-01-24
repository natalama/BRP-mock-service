package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record Address(@JsonProperty("street") String street,
                      @JsonProperty("houseNumber") String houseNumber,
                      @JsonProperty("postalCode") String postalCode,
                      @JsonProperty("city") String city,
                      @JsonProperty("country") String country

) {

    public Address {
        Objects.requireNonNull(street, "street is required");
        Objects.requireNonNull(houseNumber, "houseNumber is required");
        Objects.requireNonNull(postalCode, "postalCode is required");
        Objects.requireNonNull(city, "city is required");
        Objects.requireNonNull(country, "country is required");
    }

}