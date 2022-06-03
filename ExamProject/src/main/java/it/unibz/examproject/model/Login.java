package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Login {
    private final String mail;
    private final String password;

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public Login(@JsonProperty("email") String mail, @JsonProperty("password") String password) {
        this.mail = mail;
        this.password = password;
    }
}
