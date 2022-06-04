import it.unibz.mailclient.Operations;
import it.unibz.mailclient.model.UserPublicKey;
import it.unibz.mailclient.rsa.RSA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RegistrationOperationTest {
    private final String name = "John";
    private final String surname = "Cena";
    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private Operations operations;

    @BeforeEach
    public void setup() throws IOException {
        this.operations = new Operations(baseUrl);
        this.operations.resetDatabase();
    }

    @Test
    public void cookieIsAddedIfNewUserIsRegistered() throws IOException {
        this.operations.register(name, surname, mail, password);

        assertEquals(200, this.operations.getCon().getResponseCode());
        assertEquals(1, this.operations.getCookieManager().getCookieStore().getCookies().size());
    }

    @Test
    public void privateKeyIsAddedToStoreIfNewUserIsRegistered() throws IOException {
        this.operations.register(name, surname, mail, password);

        assertEquals(1, this.operations.getPrivateKeys().size());
    }

    @Test
    public void encryptionAndDecryptionWorksWithNewKeysTest() throws IOException {
        this.operations.register(name, surname, mail, password);
        UserPublicKey publicKey = this.operations.getUserPublicKey(mail);

        String plainText = "hello";
        int[] cipherText = new RSA().encrypt(plainText, publicKey.getPublicKey());
    }

    @Test
    public void cookieIsNotReturnedOnInvalidRegistration() throws IOException {
        this.operations.register(name, surname, "invalidEmail", password);

        assertEquals(0, this.operations.getCookieManager().getCookieStore().getCookies().size());
    }

    @Test
    public void cookieDoesNotChangeIfAlreadyLoggedIn() throws IOException {

        this.operations.register(name, surname, mail, password);
        String initialCookieValue = this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();

        this.operations.register(name, surname, mail, password);
        assertEquals(initialCookieValue, this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue());
    }
}
