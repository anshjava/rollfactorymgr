package ru.kamuzta.rollfactorymgr.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Small Util class to operate with json files
 */
@AllArgsConstructor
public class JsonUtil {

    private final ObjectMapper mapper;

    public static JsonUtil getInstance() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule customDateModule = new JavaTimeModule();
        customDateModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
            @Override
            public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(offsetDateTime));
            }
        });

        customDateModule.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
            @Override
            public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return OffsetDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(p.getText()));

            }
        });
        objectMapper.registerModule(customDateModule).disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return new JsonUtil(objectMapper);
    }

    public static JsonUtil getInstance(JsonInclude.Include include) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JsonUtil(objectMapper.setSerializationInclusion(include));
    }

    public <E extends Throwable> String writeObject(Object jsonObject, Function<Exception, E> func) throws E {
        try {
            return mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw func.apply(e);
        }
    }

    public <E extends Throwable> String writeObject(Object jsonObject,
                                                    BiFunction<String, Exception, E> func, String message) throws E {
        try {
            return mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw func.apply(message, e);
        }
    }

    public <T, E extends Throwable> T readValue(String json, Class<T> targetClass, Function<Exception, E> func) throws E {
        try {
            return mapper.readValue(json, targetClass);
        } catch (IOException e) {
            throw func.apply(e);
        }
    }

    public <T, E extends Throwable> T readValue(String json, Class<T> targetClass,
                                                BiFunction<String, Exception, E> func, String message) throws E {
        try {
            return mapper.readValue(json, targetClass);
        } catch (IOException e) {
            throw func.apply(message, e);
        }
    }

    private InputStream getResource(String fileName) {
        return this.getClass().getResourceAsStream("/json/" + fileName);
    }

    public <T, E extends Throwable> T getObjectFromJson(String fileName, Class<T> targetClass, Function<Exception, E> func) throws E {
        try {
            return mapper.readerFor(targetClass).readValue(getResource(fileName));
        } catch (IOException e) {
            throw func.apply(e);
        }
    }

    public <T, E extends Throwable> List<T> getListFromJson(String fileName, Class<T> targetClass, Function<Exception, E> func) throws E {
        try {
            return mapper.readerForListOf(targetClass).readValue(getResource(fileName));
        } catch (IOException e) {
            throw func.apply(e);
        }
    }

    public <T, E extends Throwable> List<T> getMapFromJson(String fileName, Class<T> targetClass, Function<Exception, E> func) throws E {
        try {
            return mapper.readerForMapOf(targetClass).readValue(getResource(fileName));
        } catch (IOException e) {
            throw func.apply(e);
        }
    }

    public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        public ZonedDateTimeDeserializer() {
        }

        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return ZonedDateTime.parse(p.getText());
        }
    }

}
