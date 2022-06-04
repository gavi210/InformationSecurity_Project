package it.unibz.examproject.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.unibz.examproject.model.Registration;
import it.unibz.examproject.util.JsonOperations;
import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
                System.out.println(requestBody);
                Registration registration = JsonOperations.getObject(requestBody, Registration.class);

                System.out.println(registration.toString());
                if (UserInputValidator.isNameValid(registration.getName()) && UserInputValidator.isSurnameValid(registration.getSurname())
                        && UserInputValidator.isEmailAddressValid(registration.getEmail()) && UserInputValidator.isPasswordValid(registration.getPassword())
                        && UserInputValidator.isPublicKeyValid(registration.getPublicKey())) {
                    boolean emailAlreadyInUse = repository.emailAlreadyInUse(registration.getEmail());

                    if (emailAlreadyInUse) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email already in use!");
                    } else {

                        HttpSession newSession = request.getSession();
                        /* assume that the Cookie never expires until the session is invalidated through logout mechanism */
                        // newSession.setMaxInactiveInterval(3600);

                        Authentication.setUserSession(newSession, registration.getEmail());

                        repository.registerNewUser(registration.getName(), registration.getSurname(), registration.getEmail(),
                                registration.getPassword(), registration.getPublicKey().getVal(), registration.getPublicKey().getN());
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
