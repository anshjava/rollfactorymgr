package ru.kamuzta.rollfactorymgr.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.kamuzta.rollfactorymgr.rest.AnswerDetail;

@Getter
public class RestException extends RuntimeException {

    AnswerDetail answerDetail;
    String method;
    String url;
    int status;
    String reason;

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String method, String url, Throwable cause) {
        super("request: " + method + "\n" +
              url + "\n\n", cause);

        this.method = method;
        this.url = url;
    }

    public RestException(String method, String url, int status, String reason) {
        super("request: " + method + "\n" +
              url + "\n\n" +
              "response: " + status + " " + reason);

        this.method = method;
        this.url = url;
        this.status = status;
        this.reason = reason;
    }

    public RestException(AnswerDetail answerDetail) {
        super("request: " + answerDetail.getMethod() + "\n" +
              answerDetail.getUrl() + "\n\n" +
              "response: " + answerDetail.getStatus() + " " + answerDetail.getReason() + "\n\n"
              + StringEscapeUtils.unescapeJava(answerDetail.getAnswerRaw()));

        this.method = answerDetail.getMethod();
        this.url = answerDetail.getUrl();
        this.status = answerDetail.getStatus();
        this.reason = answerDetail.getReason();
        this.answerDetail = answerDetail;
    }
}
