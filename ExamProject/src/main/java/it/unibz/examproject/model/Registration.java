package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public class Registration {

    private final String name;
    private final String surname;
    private final String email;
    private final String password;
    private final PublicKey publicKey;

    public Registration(@JsonProperty("name") String name, @JsonProperty("surname") String surname,
                        @JsonProperty("email") String email, @JsonProperty("password") String password,
                        @JsonProperty("publicKey") PublicKey publicKey) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
