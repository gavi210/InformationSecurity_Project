package it.unibz.examproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicKey {
    private final int val;
    private final int n;

    public int getVal() {
        return val;
    }

    public int getN() {
        return n;
    }

    public PublicKey(@JsonProperty("val") int d, @JsonProperty("n") int n) {
        this.val = d;
        this.n = n;
    }
}
