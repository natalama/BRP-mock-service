package nl.pink.mocks.brp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Address details")
public record Address(
        @JsonProperty("street") @NotBlank @Schema(description = "Street name", example = "Main Street") String street,
        @JsonProperty("houseNumber") @NotBlank @Schema(description = "House number", example = "123") String houseNumber,
        @JsonProperty("postalCode") @NotBlank @Schema(description = "Postal code", example = "1234AB") String postalCode,
        @JsonProperty("city") @NotBlank @Schema(description = "City", example = "Amsterdam") String city,
        @JsonProperty("country") @NotBlank @Size(max = 2) @Schema(description = "2-character country code", example = "NL") String country
) {

    public Address {
        Objects.requireNonNull(street, "street is required");
        Objects.requireNonNull(houseNumber, "houseNumber is required");
        Objects.requireNonNull(postalCode, "postalCode is required");
        Objects.requireNonNull(city, "city is required");
        Objects.requireNonNull(country, "country is required");
    }

}