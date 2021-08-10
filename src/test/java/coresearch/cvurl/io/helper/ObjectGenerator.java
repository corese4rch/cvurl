package coresearch.cvurl.io.helper;

import coresearch.cvurl.io.helper.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;

public class ObjectGenerator {

    private static final Random RANDOM = new Random();

    private ObjectGenerator() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", ObjectGenerator.class.getName()));
    }

    public static User generateTestObject() {
        return new User(UUID.randomUUID().toString(), RANDOM.nextInt(100));
    }

    public static List<User> generateListOfTestObjects() {
        var userList = new ArrayList<User>(3);

        userList.add(generateTestObject());
        userList.add(generateTestObject());
        userList.add(generateTestObject());

        return userList;
    }
}
