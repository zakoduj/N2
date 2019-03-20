package Bitfinex;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JsonDeserialize(using = Part.Deserializer.class)
class Part {
    private final Object objectValue;
    private final Object[][] arrayValue;

    private Part(Object[][] arrayValue) {
        this.objectValue = null;
        this.arrayValue = arrayValue;
    }

    private Part(Object objectValue) {
        this.objectValue = objectValue;
        this.arrayValue = null;
    }

    <T> T get(Class<T> type) {
        return type.cast(this.objectValue);
    }

    <T> boolean is(Class<T> type) {
        return type.isInstance(this.objectValue);
    }

    void get(Consumer<Part[]> objectValue) {
        if (this.arrayValue != null) {
            for (Object[] objects : this.arrayValue) {
                List<Part> partList = new ArrayList<>();
                for (Object object : objects) {
                    partList.add(new Part(object));
                }
                objectValue.accept(partList.toArray(new Part[0]));
            }
        }
    }

    boolean isComplex() {
        return this.arrayValue != null;
    }

    static class Deserializer extends JsonDeserializer<Part> {
        @Override
        public Part deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                return new Part(jsonParser.readValueAs(Object[][].class));
            }
            return new Part(jsonParser.readValueAs(Object.class));
        }
    }
}
