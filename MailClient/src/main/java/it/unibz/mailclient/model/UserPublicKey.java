package it.unibz.mailclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPublicKey {
    private final int publicKey;
    private final int n;

    public int getPublicKey() {
        return publicKey;
    }

    public int getN() {
        return n;
    }

    public UserPublicKey(@JsonProperty("publicKey") int publicKey, @JsonProperty("n") int n) {
        this.publicKey = publicKey;
        this.n = n;
    }
}
