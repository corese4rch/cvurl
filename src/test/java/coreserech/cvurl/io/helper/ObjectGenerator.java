package coreserech.cvurl.io.helper;

import coreserech.cvurl.io.helper.model.User;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class ObjectGenerator {

    public static User generateTestObject() {
        return new PodamFactoryImpl().manufacturePojo(User.class);
    }

    public static List<User> generateListOfTestObjects() {
        List<User> userList = new ArrayList<>(3);
        userList.add(generateTestObject());
        userList.add(generateTestObject());
        userList.add(generateTestObject());
        return userList;
    }
}
