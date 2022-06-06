import it.unibz.mailclient.rsa.EuclideanAlgorithm;
import it.unibz.mailclient.rsa.RSA;
import it.unibz.mailclient.rsa.RSAKeyPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RSATest {

    private RSAKeyPair keys;
    private RSA rsa;

    @BeforeEach
    public void setUp() {
        rsa = new RSA();

        keys = rsa.generateKeys();
    }

    @Test
    public void decryptMessage() {
        String plaintext = "aaaa";

        for(int i = 0; i < 100; i++) {
            keys = rsa.generateKeys();
            int[] ciphertext = rsa.encrypt(plaintext, keys.getPublicKey().getVal(), keys.getPublicKey().getN());

            String decrypted_message = rsa.decrypt(ciphertext, keys.getPrivateKey().getVal(), keys.getPrivateKey().getN());

            assertEquals(plaintext, decrypted_message);
        }
    }

    @Test
    public void euclideanAlgorithmTest() {
        assertEquals(611, EuclideanAlgorithm.getPrivateKeyValue(11, 1680));
    }

    
}
