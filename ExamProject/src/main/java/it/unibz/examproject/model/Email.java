package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Email {

    private String sender;
    private final String receiver;
    private final String subject;
    private final String body;
    private String timestamp;

    public Email(
            @JsonProperty("receiver") String receiver,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body) {
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
    }

    public Email(
            @JsonProperty("sender") String sender,
            @JsonProperty("receiver") String receiver,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body,
            @JsonProperty("timestamp") String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
    }

    /*
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

     */

    public String getSender() {
        return sender;
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

    public String getTimestamp() {
        return timestamp;
    }
}
