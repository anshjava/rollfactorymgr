package ru.kamuzta.rollfactorymgr.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.exception.RestException;

public class RestUtils {
    private final ObjectMapper mapper;
    private static RestUtils instance;

    private RestUtils() {
        //To serialize OffsetDateTime into string timestamp
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static RestUtils get() {
        if (RestUtils.instance == null) {
            instance = new RestUtils();
        }
        return instance;
    }

    public static ObjectMapper mapper() {
        return get().mapper;
    }

    @NotNull
    public static RestException getRestException() {
        return new RestException(new Exception("msg")); //TODO
    }
}
