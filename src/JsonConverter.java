import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonConverter {

    public <T> T fromJsonString(String json, Class<T> tClass) throws IOException {
        ObjectReader objectReader = getObjectReader(tClass);
        return objectReader.readValue(json);
    }

    public <T> String toJsonString(T obj, Class<T> tClass) throws JsonProcessingException {
        return getObjectWriter(tClass).writeValueAsString(obj);
    }

    private ObjectReader reader;
    private ObjectWriter writer;

    private <T> void instantiateMapper(Class<T> tClass) {
        ObjectMapper mapper = new ObjectMapper();
        this.reader = mapper.readerFor(tClass);
        this.writer = mapper.writerFor(tClass);
    }

    private <T> ObjectReader getObjectReader(Class<T> tClass) {
        if (this.reader == null) instantiateMapper(tClass);
        return this.reader;
    }

    private <T> ObjectWriter getObjectWriter(Class<T> tClass) {
        if (this.writer == null) instantiateMapper(tClass);
        return this.writer;
    }
}