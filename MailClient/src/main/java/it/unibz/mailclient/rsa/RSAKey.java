package it.unibz.mailclient.rsa;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RSAKey {
    private int val;
    private int n;

    public RSAKey(@JsonProperty("val") int d, @JsonProperty("n") int n) {
        this.val = d;
        this.n = n;
    }

    public int getVal() {
        return this.val;
    }

    public int getN() {
        return this.n;
    }
}
