package ru.kamuzta.rollfactorymgr.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Detailed response with all headers and response body
 */
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class AnswerDetail {

    String method;

    String url;

    int status;

    String reason;

    Map<String, List<String>> requestHeaders = new LinkedHashMap<>();

    Map<String, List<String>> responseHeaders = new LinkedHashMap<>();

    String answerRaw;

    Object answerParsed;

    public <RESULT_CLASS> RESULT_CLASS getAnswer() {
        return (RESULT_CLASS) answerParsed;
    }

    public <INTERNAL_ERROR_CLASS> INTERNAL_ERROR_CLASS getInternalServerError() {
        return (INTERNAL_ERROR_CLASS) answerParsed;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeaders.clear();
        this.requestHeaders.putAll(requestHeaders);
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders.clear();
        this.responseHeaders.putAll(responseHeaders);
    }

    /**
     * In cases of non-standard error codes not described in swagger .
     */
    @SuppressWarnings("unchecked")
    public <T> T getAnswerParsed(Class<T> clazz) {
        if (answerParsed != null && clazz.isAssignableFrom(answerParsed.getClass())) {
            return (T) answerParsed;
        } else if (answerRaw != null) {
            try {
                return RestUtils.mapper().readValue(answerRaw, clazz);
            } catch (IOException e) {
                log.error("Error parsing {} to {}", answerRaw, clazz);
                return null;
            }
        } else {
            return null;
        }
    }
}
