package it.unibz.mailclient.rsa;

public class RSAKey {
    private int[] v;

    public RSAKey(int val, int n) {
        this.v = new int[2];
        this.v[0] = val;
        this.v[1] = n;
    }

    public int getVal() {
        return this.v[0];
    }

    public int getN() {
        return this.v[1];
    }
}
