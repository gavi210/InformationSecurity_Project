import it.unibz.mailclient.Operations;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class RegistrationOperation {
    private final String name = "John";
    private final String surname = "Cena";
    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";

    @Test
    public void newUserIsRegistered() throws IOException {
        Operations operations = new Operations(baseUrl);

        operations.register(name, surname, mail, password);
    }

    @Test
    public void cookieDoesNotChangeIfAlreadyLoggedIn() throws IOException {
        Operations operations = new Operations(baseUrl);

        operations.register(name, surname, mail, password);

        String cookieId_1 = operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();
        assertEquals(200, operations.getCon().getResponseCode());
        operations.login(mail,password);
        String cookieId_2 = operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();

        assertEquals(400, operations.getCon().getResponseCode());
        assertEquals(cookieId_1, cookieId_2);
    }
}
