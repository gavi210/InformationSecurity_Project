import it.unibz.mailclient.Operations;
import it.unibz.mailclient.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        this.operations.sendEmail(mail, sampleSubject, sampleBody, false);

        assertEquals(1, this.operations.getInboxEmails().size());
    }

    @Test
    public void testGetSentEmails() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody, true);

        assertEquals(1, this.operations.getInboxEmails().size());
    }

    @Test
    public void printGetSentEmails() throws IOException {
        String name_receiver = "AnotherJohn";
        String surname_receiver = "AnotherCena";
        String mail_receiver = "receiver@gmail.com";
        String password_receiver = "YouCantSeeMe1!";
        this.operations.register(name, surname, mail, password);
        this.operations.logout();
        this.operations.register(name_receiver, surname_receiver, mail_receiver, password_receiver);
        this.operations.logout();

        this.operations.login(mail, password);
        this.operations.sendEmail(mail_receiver, sampleSubject, sampleBody, false);

        this.operations.logout();
        this.operations.login(mail_receiver, password_receiver);

        List<Email> inboxReceiver = this.operations.getInboxEmails();
        assertEquals(1, inboxReceiver.size());
        assertEquals(sampleBody, inboxReceiver.get(0).getBody());
        System.out.println("Initial: " + sampleBody);
        System.out.println("Received: " + inboxReceiver.get(0).getBody());
    }

    @Test
    public void emailsWithSignatureAreCorrectlyDeserialized() throws IOException {
        this.operations.register(name, surname, mail, password);
        this.operations.sendEmail(mail, sampleSubject, sampleBody, true);

        System.out.println(this.operations.getInboxEmails().get(0).getSignature());
    }

}
