package bepicky.service.perf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class UtilObjectMapper {
    
    private ObjectMapper om = new ObjectMapper();

    public UtilObjectMapper() {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T read(byte[] data, Class<T> zz) {
        try {
            return om.readValue(data, zz);
        } catch (IOException e) {
            throw new IllegalStateException("invalid data");
        }
    }

    public String writeString(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("invalid data");
        }
    }

    public byte[] writeData(Object value) {
        try {
            return om.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("invalid data");
        }
    }
}
