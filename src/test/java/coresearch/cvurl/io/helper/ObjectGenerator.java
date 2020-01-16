package coresearch.cvurl.io.helper;

import coresearch.cvurl.io.helper.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;

public class ObjectGenerator {

    private static Random random = new Random();

    private ObjectGenerator() {
        throw new IllegalStateException(format("Creating of class %s is forbidden", ObjectGenerator.class.getName()));
    }

    public static User generateTestObject() {
        return new User(UUID.randomUUID().toString(), random.nextInt(100));
    }

    public static List<User> generateListOfTestObjects() {
        List<User> userList = new ArrayList<>(3);
        userList.add(generateTestObject());
        userList.add(generateTestObject());
        userList.add(generateTestObject());
        return userList;
    }
}
