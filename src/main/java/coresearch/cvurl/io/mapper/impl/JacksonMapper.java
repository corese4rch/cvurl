package coresearch.cvurl.io.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coresearch.cvurl.io.exception.MappingException;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.mapper.GenericMapper;

import java.io.IOException;

/**
 * The default implementation of the {@link GenericMapper} class. It uses an instance of {@link ObjectMapper} as a converter.
 *
 * @since 0.9
 */
public class JacksonMapper extends GenericMapper {

    private final ObjectMapper objectMapper;

    public JacksonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        try {
            return this.objectMapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T readValue(String value, BodyType<T> valueType) {
        try {
            return this.objectMapper.readValue(value,
                    this.objectMapper.getTypeFactory().constructType(valueType.getType()));
        } catch (IOException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    @Override
    public String writeValue(Object value) {
        try {
            return this.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }
}
