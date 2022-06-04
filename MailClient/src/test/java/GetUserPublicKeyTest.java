import it.unibz.mailclient.Operations;
import it.unibz.mailclient.model.UserPublicKey;
import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;

        import java.io.IOException;

        import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetUserPublicKeyTest {

    private final String name = "John";
    private final String surname = "Cena";
    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final int userPublicKey = 1;
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private final String sampleSubject = "Hello To Myself";
    private final String sampleBody = "Hello Again";
    private Operations operations;

    @BeforeEach
    public void setup() throws IOException {
        this.operations = new Operations(baseUrl);
        operations.resetDatabase();
    }

    @Test
    public void testEndPointWorks() throws IOException {
        this.operations.register(name, surname, mail, password, userPublicKey);
        UserPublicKey publicKey = this.operations.getUserPublicKey("john@gmail.com");
        assertEquals(userPublicKey, publicKey.getPublicKey());
    }

    @Test
    public void testNegativeKeyOnInvalidInput() throws IOException {
        UserPublicKey publicKey = this.operations.getUserPublicKey("john@gmail.com");
        assertTrue(publicKey.getPublicKey() < 0);
    }
}
