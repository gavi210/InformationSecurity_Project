import it.unibz.mailclient.Operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginOperationTest {

    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private Operations operations;

    @BeforeEach
    public void setup() {
        this.operations = new Operations(baseUrl);
    }


    @Test
    public void testCookieIsNotReturnedOnInvalidLogin() throws IOException {
        this.operations.login(mail, "someInvalidPassword");

        List<HttpCookie> cookies = this.operations.getCookieManager().getCookieStore().getCookies();
        assertEquals(0, cookies.size());
    }

    @Test
    public void testCookieIsReturnedOnSuccessfulLogin() throws IOException {
        this.operations.login(mail, password);

        List<HttpCookie> cookies = this.operations.getCookieManager().getCookieStore().getCookies();
        assertEquals(1, cookies.size());
    }


    @Test
    public void loginFailsIfUserAlreadyLoggedIn() throws IOException {

        this.operations.login(mail, password);
        assertEquals(200, this.operations.getCon().getResponseCode());

        this.operations.login(mail, password);
        assertEquals(400, this.operations.getCon().getResponseCode());
    }

    @Test
    public void cookieIsNotChangedOnAnotherLoginAttempt() throws IOException {
        this.operations.login(mail, password);
        String initialCookieValue = this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();

        this.operations.login(mail, password);
        assertEquals(initialCookieValue, this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue());
    }
}
