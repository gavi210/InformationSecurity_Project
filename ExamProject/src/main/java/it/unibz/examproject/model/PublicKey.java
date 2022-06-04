package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicKey {
    private final int d;
    private final int n;

    public int getD() {
        return d;
    }

    public int getN() {
        return n;
    }

    public PublicKey(@JsonProperty("d") int d, @JsonProperty("n") int n) {
        this.d = d;
        this.n = n;
    }
}
