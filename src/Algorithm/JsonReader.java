package Algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonReader {
    public class Result {
        private final List<Object> result;

        Result(List<Object> result) {
            this.result = result;
        }

        public <T> T get(Class<T> type) {
            for (Object object : this.result) {
                if (type.isInstance(object)) {
                    return type.cast(object);
                }
            }
            return null;
        }

        public boolean isEmpty() {
            return this.result.isEmpty();
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader reader;

    public JsonReader() {
        this.reader = null;
    }

    public <T> JsonReader(Class<T> tClass) {
        this.reader = this.mapper.readerFor(tClass);
    }

    public Result read(String json, Map<String, Class<?>> values) {
        List<Object> result = new ArrayList<>();
        try {
            JsonNode node = this.mapper.readTree(json);
            for (Map.Entry<String, Class<?>> entry : values.entrySet()) {
                JsonNode nodeValue = node.get(entry.getKey());
                result.add(this.mapper.treeToValue(nodeValue, entry.getValue()));
            }
        } catch (IOException ignored) {
        }
        return new Result(result);
    }

    public <T> T read(String s) throws IOException {
        return this.reader.readValue(s);
    }
}
