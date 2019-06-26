package coreserech.cvurl.io.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import coreserech.cvurl.io.mapper.impl.JacksonMapper;

public class MapperFactory {

    public static GenericMapper createDefault() {
        return from(new ObjectMapper());
    }

    public static GenericMapper from(ObjectMapper objectMapper) {
        return new JacksonMapper(objectMapper);
    }
}
