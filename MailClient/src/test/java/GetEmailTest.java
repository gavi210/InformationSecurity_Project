import it.unibz.mailclient.Operations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetEmailTest {

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
    public void testGetInboxEmails() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody);

        assertEquals(1, this.operations.getInboxEmails().size());
    }

    @Test
    public void testGetSentEmails() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody);

        assertEquals(1, this.operations.getSentEmails().size());
    }
}
