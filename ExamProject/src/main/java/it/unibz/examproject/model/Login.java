package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Login {
    private final String email;
    private final String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Login(@JsonProperty("email") String email, @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }
}
