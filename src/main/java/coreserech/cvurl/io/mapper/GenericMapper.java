package coreserech.cvurl.io.mapper;

public interface GenericMapper {

    <T> T readValue(String value, Class<T> valueType);

    String writeValue(Object value);
}
