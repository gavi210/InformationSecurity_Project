package it.unibz.mailclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailForSendMailRequest {

    private final String receiver;
    private final String subject;
    private final String body;
    private final String signature;

    public EmailForSendMailRequest(
            @JsonProperty("receiver") String receiver,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body,
            @JsonProperty("signature") String signature) {
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
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

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
