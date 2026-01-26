package nl.pink.mocks.brp.utils;

import net.datafaker.Faker;
import nl.pink.mocks.brp.domain.Address;
import nl.pink.mocks.brp.domain.Person;
import nl.pink.mocks.brp.domain.User;

import java.util.Locale;

public class FakerFactory {

    public static Person createRandomPerson() {
        return createRandomPerson(Locale.of("nl-NL"));
    }

    public static User createRandomUser() {
        return createRandomUser(Locale.of("nl-NL"));
    }

    public static Address createRandomAddress() {
        return createRandomAddress(Locale.of("nl-NL"));
    }

    public static Person createRandomPerson(Locale locale) {
        return new Person(createRandomAddress(locale), createRandomUser(locale));
    }

    public static Address createRandomAddress(Locale locale) {
        if (locale == null) {
            locale = Locale.of("nl-NL");
        }
        Faker faker = new Faker(locale);
        return new Address(
                faker.address().streetName(),
                faker.address().buildingNumber(),
                faker.address().zipCode(),
                faker.address().city(),
                faker.country().currencyCode()
        );
    }

    public static User createRandomUser(Locale locale) {
        if (locale == null) {
            locale = Locale.of("nl-NL");
        }
        Faker faker = new Faker(locale);
        String bsn = firstValidBsn(faker);
        return new User(
                bsn,
                faker.name().firstName(),
                faker.name().lastName(),
                faker.timeAndDate().birthday()
        );
    }

    private static String firstValidBsn(Faker faker) {
        String bsn = faker.numerify("#########");
        if(BsnUtils.isValidBsn(bsn)) {
            return bsn;
        }
        return firstValidBsn(faker);
    }


}
