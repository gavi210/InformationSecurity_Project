package it.unibz.examproject.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.unibz.examproject.model.Login;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.JsonOperations;
import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    public LoginServlet() {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);

        if (Authentication.isUserLogged(session)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User already logged in!");
        }
        else {
            String requestBody = request.getReader().lines().collect(Collectors.joining(""));

            Login credentials;
            try {
                credentials = JsonOperations.getObject(requestBody, Login.class);
                if (UserInputValidator.isEmailAddressValid(credentials.getEmail()) && UserInputValidator.isPasswordValid(credentials.getPassword())) {
                    if (repository.areCredentialsValid(credentials.getEmail(), credentials.getPassword())) {
                        HttpSession newSession = request.getSession();
                        /* assume that the Cookie never expires until the session is invalidated through logout mechanism */
                        // newSession.setMaxInactiveInterval(3600);

                        Authentication.setUserSession(newSession, credentials.getEmail());

                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Check input correctness!");
                }
            } catch(JsonProcessingException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed request body");
            }
        }
    }
}
