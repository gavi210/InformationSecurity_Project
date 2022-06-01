import it.unibz.mailclient.ParameterStringBuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSender {
    private static final String mail = "john@gmail.com";
    private static final String password = "YouCantSeeMe1!";
    private static final String urlString = "http://localhost:8080/ExamProject_war_exploded/LoginServlet";

    private static final CookieManager cookieManager = new CookieManager();
    private static HttpURLConnection con;

    public static void main(String[] args) throws IOException {
        URL url = new URL(urlString);
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
        cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));

        System.out.println("Cookie Name: " + cookies.get(0).getValue());

        con.disconnect();
    }
}
