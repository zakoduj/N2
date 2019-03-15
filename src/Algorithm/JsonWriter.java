package Algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonWriter {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer;
    public <T> JsonWriter(Class<T> tClass) {
        this.writer = this.mapper.writerFor(tClass);
    }
    public String write(Object object) throws JsonProcessingException {
        return this.writer.writeValueAsString(object);
    }
}
