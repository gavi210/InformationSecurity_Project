package it.unibz.mailclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.mailclient.model.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Operations {
    private final CookieManager cookieManager;
    private final String urlString;

    private HttpURLConnection con;

    public enum GetEmailsOperation { inbox, sent }

    public Operations(String urlString) {
        this.urlString = urlString;
        this.cookieManager = new CookieManager();
    }

    public void login(String mail, String password) throws IOException {
        getNewPostConnection(urlString + "/LoginServlet");

        addCookiesToRequest();

        Login loginCredentials = new Login(mail, password);
        writeRequestBody(loginCredentials);

        submitRequest();
        refreshCookies();
    }



    public void register(String name, String surname, String mail, String password, int userPublicKey) throws IOException {
        getNewPostConnection(urlString + "/RegisterServlet");

        addCookiesToRequest();

        Registration registration = new Registration(name, surname, mail, password, userPublicKey);
        writeRequestBody(registration);

        refreshCookies();
    }

    public void sendEmail(String receiver, String subject, String body) throws IOException {
        getNewPostConnection(urlString + "/SendMailServlet");

        addCookiesToRequest();

        EmailForSendMailRequest email = new EmailForSendMailRequest(receiver, subject, body);
        writeRequestBody(email);

        refreshCookies();
    }

    public List<Email> getInboxEmails() throws IOException {
        getNewGetRequest(urlString + "/GetInboxMailServlet");

        addCookiesToRequest();

        refreshCookies();

        String body = new String(this.con.getInputStream().readAllBytes());

        return new ObjectMapper().readValue(body, new TypeReference<List<Email>>() {});
    }

    public UserPublicKey getUserPublicKey(String userEmail) throws IOException {
        String parametrizedUrl = String.format(urlString + "/GetUserPublicKeyServlet?email=%s", userEmail);
        getNewGetRequest(parametrizedUrl);

        addCookiesToRequest();

        refreshCookies();

        String body = new String(this.con.getInputStream().readAllBytes());

        return new ObjectMapper().readValue(body, UserPublicKey.class);
    }

    public List<Email> getSentEmails() throws IOException {
        getNewGetRequest(urlString + "/GetSentMailServlet");

        addCookiesToRequest();

        refreshCookies();

        String body = new String(this.con.getInputStream().readAllBytes());

        return new ObjectMapper().readValue(body, new TypeReference<List<Email>>() {});
    }

    public void logout() throws IOException {
        getNewPostConnection(urlString + "/LogoutServlet");

        addCookiesToRequest();

        removeOldCookies();
    }

    public void resetDatabase() throws IOException {
        getNewPostConnection(urlString + "/ResetDatabaseServlet");
        // consume the response code, it ensures that the request is sent and processed by the server
        submitRequest();
    }

    private void submitRequest() throws IOException {
        this.con.getResponseCode();
    }

    private void refreshCookies() throws IOException {
        String cookiesHeader = this.con.getHeaderField("Set-Cookie");
        if(cookiesHeader != null) {
            List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
            cookies.forEach(cookie -> this.cookieManager.getCookieStore().add(null, cookie));
        }
    }

    private void writeRequestBody(Object obj) throws IOException {
        this.con.setDoOutput(true);
        OutputStream stream = this.con.getOutputStream();
        stream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
    }

    private void getNewPostConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        this.con = (HttpURLConnection) url.openConnection();
        this.con.setRequestMethod("POST");
        this.con.setDoOutput(true);
    }

    private void getNewGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        this.con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
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

    private void removeOldCookies() {
        this.cookieManager.getCookieStore().removeAll();
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
