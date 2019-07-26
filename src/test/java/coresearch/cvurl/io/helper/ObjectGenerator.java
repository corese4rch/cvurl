package coresearch.cvurl.io.helper;

import coresearch.cvurl.io.helper.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ObjectGenerator {

    private static Random random = new Random();

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
