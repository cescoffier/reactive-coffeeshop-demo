package me.escoffier.quarkus.coffeeshop;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Names {

    private static final List<String> VALUES = Arrays.asList(
            "Olivia",
            "Oliver",
            "Amelia",
            "George",
            "Isla",
            "Harry",
            "Ava",
            "Noah",
            "Emily",
            "Jack",
            "Sophia",
            "Charlie",
            "Grace",
            "Leo",
            "Mia",
            "Jacob",
            "Poppy",
            "Freddie",
            "Ella",
            "Alfie",
            "Tom",
            "Julie",
            "Matt",
            "Joe",
            "Zoe"
    );

    static String pickAName() {
        Random random = new Random();
        int index = random.nextInt(VALUES.size());
        return VALUES.get(index);
    }

    private Names() {
        // avoid direct instantiation
    }
}
