package coresearch.cvurl.io.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JacksonMapperTest {

    private static JacksonMapper jacksonMapper;

    @BeforeClass
    public static void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonMapper = new JacksonMapper(mapper);
    }

    @Test
    public void writeValueTest() throws JsonProcessingException {

        User user = ObjectGenerator.generateTestObject();

        String actual = jacksonMapper.writeValue(user);

        String expected = new ObjectMapper().writeValueAsString(user);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void readValueTest() throws JsonProcessingException {

        User expected = ObjectGenerator.generateTestObject();

        String jsonString = new ObjectMapper().writeValueAsString(expected);

        User actual = jacksonMapper.readValue(jsonString, User.class);

        Assert.assertEquals(expected, actual);
    }

}
