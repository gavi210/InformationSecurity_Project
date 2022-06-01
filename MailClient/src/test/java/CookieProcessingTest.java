import it.unibz.mailclient.Operations;
import it.unibz.mailclient.ParameterStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class CookieProcessingTest {

    private final String mail = "john@gmail.com";
    private final String password = "YouCantSeeMe1!";
    private final String baseUrl = "http://localhost:8080/ExamProject_war_exploded";
    private CookieManager cookieManager;

    @BeforeEach
    public void setup() {
        this.cookieManager = new CookieManager();
    }
    @Test
    public void testCookieIsReturned() throws IOException {
        URL url = new URL(baseUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", mail);
        parameters.put("password", password);

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        String cookiesHeader = con.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
        con.disconnect();

        assertEquals(1, cookies.size());
    }

    @Test
    public void loginFailedIfUserAlreadyLoggedIn() throws IOException {
        URL url = new URL(baseUrl + "/LoginServlet");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", mail);
        parameters.put("password", password);

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        String cookiesHeader = con.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
        con.disconnect();
        cookies.forEach(cookie -> this.cookieManager.getCookieStore().add(null, cookie));

        // try again registration phase
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Cookie",
            join(cookieManager.getCookieStore().getCookies()));

        con.setDoOutput(true);

        // set the output stream to the new connection
        out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        assertEquals(con.getResponseCode(), 400);
    }

    @Test
    public void cookieDoesNotChangeIfAlreadyLoggedIn() throws IOException {
        Operations operations = new Operations(baseUrl);
        operations.login(mail,password);
        String cookieId_1 = operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();
        assertEquals(200, operations.getCon().getResponseCode());
        operations.login(mail,password);
        String cookieId_2 = operations.getCookieManager().getCookieStore().getCookies().get(0).getValue();

        assertEquals(400, operations.getCon().getResponseCode());
        assertEquals(cookieId_1, cookieId_2);
    }

    private static <T> String join(List<T> objects) {
        StringBuilder builder = new StringBuilder();

        objects.forEach(obj -> builder.append(obj.toString()).append(";"));
        return builder.toString();
    }
}
