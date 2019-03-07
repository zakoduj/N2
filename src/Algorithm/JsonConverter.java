package Algorithm;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonConverter {
    private final ObjectReader reader;
    private final ObjectWriter writer;

    public <T> JsonConverter(Class<T> tClass) {
        ObjectMapper mapper = new ObjectMapper();
        this.reader = mapper.readerFor(tClass);
        this.writer = mapper.writerFor(tClass);
    }

    public <T> T fromJsonString(String json) throws IOException {
        return this.reader.readValue(json);
    }

    public <T> String toJsonString(T obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}