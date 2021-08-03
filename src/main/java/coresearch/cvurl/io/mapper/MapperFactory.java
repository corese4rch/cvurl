package coresearch.cvurl.io.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import coresearch.cvurl.io.mapper.impl.JacksonMapper;

import static java.lang.String.format;

/**
 * The factory with handy methods for creating a {@link GenericMapper} class instance.
 *
 * @since 0.9
 */
public class MapperFactory {

    /**
     * Creates a default implementation of the {@link GenericMapper} class.
     *
     * @return the default implementation of the {@link GenericMapper} class
     */
    public static GenericMapper createDefault() {
        return from(new ObjectMapper());
    }

    /**
     * Creates a {@link JacksonMapper} class instance from the specified {@link ObjectMapper} instance.
     *
     * @param objectMapper - the mapper from which the {@link JacksonMapper} class instance will be created.
     * @return an instance of the {@link JacksonMapper} class
     */
    public static GenericMapper from(ObjectMapper objectMapper) {
        return new JacksonMapper(objectMapper);
    }

    private MapperFactory() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", MapperFactory.class.getName()));
    }
}
