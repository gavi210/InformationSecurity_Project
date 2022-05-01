package it.unibz.examproject.util.db;

public class Email {

    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private String timestamp;

    public Email(String sender, String receiver, String subject, String body, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
    }

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
