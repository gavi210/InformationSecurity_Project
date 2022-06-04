package it.unibz.mailclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPublicKey {
    private final int publicKey;

    public int getPublicKey() {
        return publicKey;
    }

    public UserPublicKey(@JsonProperty("publicKey") int publicKey) {
        this.publicKey = publicKey;
    }
}
