package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailFromSendMailRequest {

    private final String receiver;
    private final String subject;
    private final String body;

    public EmailFromSendMailRequest(
            @JsonProperty("receiver") String receiver,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body) {
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
