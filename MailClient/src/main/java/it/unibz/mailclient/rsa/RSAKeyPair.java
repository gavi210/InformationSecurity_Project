package it.unibz.mailclient.rsa;

public class RSAKeyPair {
    private final RSAKey publicKey;
    private final RSAKey privateKey;

    public RSAKeyPair(int n, int d, int e) {
        this.publicKey = new RSAKey(e, n);
        this.privateKey = new RSAKey(d, n);
    }

    public RSAKey getPublicKey() {
        return publicKey;
    }

    public RSAKey getPrivateKey() {
        return privateKey;
    }
}
