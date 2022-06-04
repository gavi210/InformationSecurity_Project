package it.unibz.mailclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.mailclient.rsa.RSAKey;

public class Registration {

    private final String name;
    private final String surname;
    private final String email;
    private final String password;
    private final RSAKey publicKey;

    public Registration(String name, String surname, String email, String password, RSAKey publicKey) {
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

    public RSAKey getPublicKey() {
        return publicKey;
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
