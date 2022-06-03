package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Registration {

    private final String name;
    private final String surname;
    private final String mail;
    private final String password;

    public Registration(@JsonProperty("name") String name, @JsonProperty("surname") String surname,
                        @JsonProperty("email") String mail, @JsonProperty("password") String password) {
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
}
