import it.unibz.mailclient.Operations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogoutOperationTest {

    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private Operations operations;

    @BeforeEach
    public void setup() {
        this.operations = new Operations(baseUrl);
    }


    @Test
    public void cookieIsRemovedAfterLogout() throws IOException {
        this.operations.login(mail, password);

        assertEquals(1, this.operations.getCookieManager().getCookieStore().getCookies().size());

        this.operations.logout();
        assertEquals(0, this.operations.getCookieManager().getCookieStore().getCookies().size());
    }

    @Test
    public void logoutWorksEvenWithNoPreviouslyLoggedUser() {
        assertDoesNotThrow(() -> this.operations.logout());
    }
}
