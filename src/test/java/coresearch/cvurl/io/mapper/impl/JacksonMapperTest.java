package coresearch.cvurl.io.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonMapperTest {

    private static JacksonMapper jacksonMapper;

    @BeforeAll
    public static void setUp() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonMapper = new JacksonMapper(mapper);
    }

    @Test
    void shouldWriteToValidJson() throws JsonProcessingException {
        //given
        var user = ObjectGenerator.generateTestObject();
        var expected = new ObjectMapper().writeValueAsString(user);

        //when
        var actual = jacksonMapper.writeValue(user);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void shouldReadToUserWhenJsonIsValid() throws JsonProcessingException {
        //given
        var expected = ObjectGenerator.generateTestObject();
        var jsonString = new ObjectMapper().writeValueAsString(expected);

        //when
        var actual = jacksonMapper.readValue(jsonString, User.class);

        //then
        assertEquals(expected, actual);
    }

}
