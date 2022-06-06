import it.unibz.mailclient.Operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SendEmailOperationTest {

    private final String name = "John";
    private final String surname = "Cena";
    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
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
    public void unauthorizedResponseIfNotLoggedIn() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.logout();
        this.operations.sendEmail(mail, sampleSubject, sampleBody, false);

        assertEquals(401, this.operations.getCon().getResponseCode());
    }

    @Test
    public void testMailIsCorrectlySent() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody, false);
        assertEquals(200, this.operations.getCon().getResponseCode());
    }

    @Test
    public void testSendingTwoMailsOneAfterTheOther() throws IOException {

        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody, false);
        this.operations.sendEmail(mail, sampleSubject, sampleBody, false);
        assertEquals(200, this.operations.getCon().getResponseCode());
    }

    @Test
    public void cookieIsChangedAfterLogoutAndLoginWithSameAccount() throws IOException {
        this.operations.register(name, surname, mail, password);
        String initialCookieValue = this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();
        this.operations.logout();

        this.operations.login(mail, password);
        assertNotEquals(initialCookieValue, this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue());
    }

    @Test
    public void cookieIsNotChangedOnFailedLoginAttempt() throws IOException {
        this.operations.register(name, surname, mail, password);
        String initialCookieValue = this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();
        // this.operations.logout();

        this.operations.login(mail, password);
        assertEquals(initialCookieValue, this.operations.getCookieManager().getCookieStore().getCookies().get(0).getValue());
    }
}
