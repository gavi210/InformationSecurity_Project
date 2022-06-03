package it.unibz.examproject.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.unibz.examproject.model.Login;
import it.unibz.examproject.model.Registration;
import it.unibz.examproject.util.JsonOperations;
import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PasswordSecurity;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    public RegisterServlet() {
        super();
    }

    public void init() {
        try {
            Properties configProperties = new Properties();
            configProperties.load(getServletContext().getResourceAsStream("/dbConfig.properties"));

            String dbms = configProperties.getProperty("db.dbms");
            if ("postgres".equals(dbms))
                repository = new PostgresRepository();
            else
                repository = new SQLServerRepository();

            repository.init(configProperties);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);

        if (Authentication.isUserLogged(session)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User already logged in!");
        } else {

            try {
                String requestBody = request.getReader().lines().collect(Collectors.joining(""));
                Registration registration = JsonOperations.getObject(requestBody, Registration.class);

                if (UserInputValidator.isNameValid(registration.getName()) && UserInputValidator.isSurnameValid(registration.getSurname())
                        && UserInputValidator.isEmailAddressValid(registration.getMail()) && UserInputValidator.isPasswordValid(registration.getPassword())) {
                    boolean emailAlreadyInUse = repository.emailAlreadyInUse(registration.getMail());

                    if (emailAlreadyInUse) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email already in use!");
                    } else {

                        HttpSession newSession = request.getSession();
                        /* assume that the Cookie never expires until the session is invalidated through logout mechanism */
                        // newSession.setMaxInactiveInterval(3600);

                        Authentication.setUserSession(newSession, registration.getMail());

                        repository.registerNewUser(registration.getName(), registration.getSurname(), registration.getMail(), registration.getPassword());
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Check input correctness");
                }
            } catch(JsonProcessingException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed request body");
            }

        }
    }

}
