package it.unibz.mailclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.mailclient.model.Email;
import it.unibz.mailclient.model.EmailForSendMailRequest;
import it.unibz.mailclient.model.Login;
import it.unibz.mailclient.model.Registration;
import it.unibz.mailclient.rsa.RSA;
import it.unibz.mailclient.rsa.RSAKey;
import it.unibz.mailclient.rsa.RSAKeyPair;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.isNull;

public class Operations {
    private final CookieManager cookieManager;
    private final Map<String, RSAKey> privateKeys;
    private final String urlString;
    private final RSA rsa;

    private String currentUserEmail;

    private HttpURLConnection con;

    public Operations(String urlString) {
        this.urlString = urlString;
        this.cookieManager = new CookieManager();
        this.privateKeys = new HashMap<>();
        this.rsa = new RSA();
    }

    public void login(String mail, String password) throws IOException {
        getNewPostConnection(urlString + "/LoginServlet");

        addCookiesToRequest();

        Login loginCredentials = new Login(mail, password);
        writeRequestBody(loginCredentials);

        submitRequest();
        refreshCookies();

        if(this.con.getResponseCode() == HttpURLConnection.HTTP_OK)
            this.currentUserEmail = mail;
    }

    public void register(String name, String surname, String mail, String password) throws IOException {
        getNewPostConnection(urlString + "/RegisterServlet");

        addCookiesToRequest();

        RSAKeyPair keyPair = new RSA().generateKeys();

        Registration registration = new Registration(name, surname, mail, password, keyPair.getPublicKey());
        writeRequestBody(registration);

        refreshCookies();

        if (this.con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            this.privateKeys.put(mail, keyPair.getPrivateKey());
            this.currentUserEmail = mail;
        }
    }

    public void sendEmail(String receiver, String subject, String body, boolean toBeSigned) throws IOException {

        if(isNull(receiver) || isNull(subject) || isNull(body))
            throw new RuntimeException("Invalid Input");

        RSAKey receiverPublicKey = this.getUserPublicKey(receiver);

        if(isKeyInvalid(receiverPublicKey))
            throw new RuntimeException("Receiver Key is not valid");

        String ciphertext = Arrays.toString(this.rsa.encrypt(body, receiverPublicKey.getVal(), receiverPublicKey.getN()));

        String signature = null;
        if(toBeSigned) {
            String hash = DigestUtils.sha256Hex(body);
            signature = Arrays.toString(this.rsa.encrypt(hash, receiverPublicKey.getVal(), receiverPublicKey.getN()));
        }

        getNewPostConnection(urlString + "/SendMailServlet");

        addCookiesToRequest();

        EmailForSendMailRequest email = new EmailForSendMailRequest(receiver, subject, ciphertext, signature);
        writeRequestBody(email);

        refreshCookies();
    }

    public List<Email> getInboxEmails() throws IOException {
        getNewGetRequest(urlString + "/GetInboxMailServlet");

        addCookiesToRequest();

        refreshCookies();

        String body = new String(this.con.getInputStream().readAllBytes());
        List<Email> encryptedEmails = new ObjectMapper().readValue(body, new TypeReference<List<Email>>() {});

        List<Email> decryptedEmails = new LinkedList<>();
        RSAKey currentUserKey = this.privateKeys.get(this.currentUserEmail);

        ObjectMapper mapper = new ObjectMapper();
        encryptedEmails.forEach(mail -> {
            try {
                int[] cipherBody = mapper.readValue(mail.getBody(), new TypeReference<int[]>() {});

                String decryptedBody = this.rsa.decrypt(cipherBody, currentUserKey.getVal(), currentUserKey.getN());
                decryptedEmails.add(new Email(mail.getSender(), mail.getReceiver(), mail.getSubject(), decryptedBody, mail.getTimestamp(), mail.getSignature()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        List<Email> matchingSignatureEmails = new LinkedList<>();
        decryptedEmails.forEach(mail -> {
            try {
                if(isNull(mail.getSignature()))
                    matchingSignatureEmails.add(mail);
                else {
                    int[] cipherSignature = new ObjectMapper().readValue(mail.getSignature(), new TypeReference<int[]>() {
                    });
                    String decryptedSignature = this.rsa.decrypt(cipherSignature, currentUserKey.getVal(), currentUserKey.getN());
                    String hashedBody = DigestUtils.sha256Hex(mail.getBody());
                    if (hashedBody.equals(decryptedSignature))
                        matchingSignatureEmails.add(new Email(mail.getSender(), mail.getReceiver(), mail.getSubject(), mail.getBody(), mail.getTimestamp(), decryptedSignature));

                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
        return matchingSignatureEmails;
    }

    public RSAKey getUserPublicKey(String userEmail) throws IOException {
        String parametrizedUrl = String.format(urlString + "/GetUserPublicKeyServlet?email=%s", userEmail);
        getNewGetRequest(parametrizedUrl);

        addCookiesToRequest();

        refreshCookies();

        String body = new String(this.con.getInputStream().readAllBytes());

        return new ObjectMapper().readValue(body, RSAKey.class);
    }

    public void logout() throws IOException {
        getNewPostConnection(urlString + "/LogoutServlet");

        addCookiesToRequest();

        removeOldCookies();

        if(this.con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            this.currentUserEmail = null;
        }
    }

    public void resetDatabase() throws IOException {
        getNewPostConnection(urlString + "/ResetDatabaseServlet");
        // consume the response code, it ensures that the request is sent and processed by the server
        submitRequest();
    }

    public Map<String, RSAKey> getPrivateKeys() {
        return privateKeys;
    }

    private void submitRequest() throws IOException {
        this.con.getResponseCode();
    }

    private void refreshCookies() {
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

    public HttpURLConnection getCon() {
        return con;
    }

    private boolean isKeyInvalid(RSAKey key) {
        return key.getVal() < 0 || key.getN() < 0;
    }
}
