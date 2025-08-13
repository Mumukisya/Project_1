package org.example.generators;

import com.github.javafaker.Faker;
import org.example.models.RegisterRequest;

public class UserGenerator {

    public static RegisterRequest randomUser() {
        Faker faker = new Faker();

        return new RegisterRequest()
                .setEmail(faker.internet().safeEmailAddress())
                .setPassword(faker.internet().password(8, 20))
                .setName(faker.name().firstName());
    }

    public static RegisterRequest randomNonValidEmailUser() {
        Faker faker = new Faker();

        return new RegisterRequest()
                .setEmail(faker.name().firstName())
                .setPassword(faker.internet().password(8, 20))
                .setName(faker.name().firstName());
    }
}
