import it.unibz.mailclient.Operations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RegistrationOperationTest {
    private final String name = "John";
    private final String surname = "Cena";
    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final int userPublicKey = 1;
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private Operations operations;

    @BeforeEach
    public void setup() throws IOException {
        this.operations = new Operations(baseUrl);
        this.operations.resetDatabase();
    }

    @Test
    public void newUserIsRegistered() throws IOException {
        this.operations.register(name, surname, mail, password, userPublicKey);

        assertEquals(200, this.operations.getCon().getResponseCode());
        assertEquals(1, this.operations.getCookieManager().getCookieStore().getCookies().size());

    }

    @Test
    public void cookieIsNotReturnedOnInvalidRegistration() throws IOException {
        this.operations.register(name, surname, "invalidEmail", password, userPublicKey);

        assertEquals(0, this.operations.getCookieManager().getCookieStore().getCookies().size());
    }

    @Test
    public void cookieDoesNotChangeIfAlreadyLoggedIn() throws IOException {

        this.operations.register(name, surname, mail, password, userPublicKey);
        String initialCookieValue = this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();

        this.operations.register(name, surname, mail, password, userPublicKey);
        assertEquals(initialCookieValue, this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue());
    }
}
