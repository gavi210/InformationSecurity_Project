package it.unibz.mailclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operations {
    private final CookieManager cookieManager;
    private final String urlString;

    private HttpURLConnection con;

    public Operations(String urlString) {
        this.urlString = urlString;
        this.cookieManager = new CookieManager();
    }

    public void login(String mail, String password) throws IOException {
        getNewPostConnection(urlString + "/LoginServlet");

        Map<String, String> loginParameters = new HashMap<>();
        loginParameters.put("email", mail);
        loginParameters.put("password", password);

        addCookiesToRequest();
        addRequestParameters(loginParameters);

        /* modify the class state with the new information
         * Update:
         * - cookies in the CookieManager
         * */
        addCookiesToCookieStore();
    }

    public void register(String name, String surname, String mail, String password) throws IOException {
        getNewPostConnection(urlString + "/RegisterServlet");

        Map<String, String> registerParameters = new HashMap<>();
        registerParameters.put("name", name);
        registerParameters.put("surname", surname);
        registerParameters.put("email", mail);
        registerParameters.put("password", password);

        addCookiesToRequest();
        addRequestParameters(registerParameters);

        /* modify the class state with the new information
         * Update:
         * - cookies in the CookieManager
         * */
        addCookiesToCookieStore();
    }

    private void getNewPostConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        this.con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
    }

    private void addRequestParameters(Map<String,String> parameters) throws IOException {
        this.con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(this.con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();
    }

    private void addCookiesToRequest() {
        this.con.setRequestProperty("Cookie",
                join(this.cookieManager.getCookieStore().getCookies()));
    }

    private void addCookiesToCookieStore() {
        String cookiesHeader = this.con.getHeaderField("Set-Cookie");
        if(cookiesHeader != null) {
            List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
            cookies.forEach(cookie -> this.cookieManager.getCookieStore().add(null, cookie));
        }
    }

    private static <T> String join(List<T> objects) {
        StringBuilder builder = new StringBuilder();

        objects.forEach(obj -> builder.append(obj.toString()).append(";"));
        return builder.toString();
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public String getUrlString() {
        return urlString;
    }

    public HttpURLConnection getCon() {
        return con;
    }
}
