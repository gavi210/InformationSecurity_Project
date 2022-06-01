package it.unibz.examproject.servlets;

import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import it.unibz.examproject.util.UserInputValidator;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    /**
     * @see HttpServlet#HttpServlet()
     */
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

    /**
     * onPost
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // if user logged in, DROP the request
        HttpSession session = request.getSession(false);

        if (Authentication.isUserLogged(session)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("<html><head><title>User already logged in!</title></head>");
            response.getWriter().print("<body>User already logged in!</body>");
            response.getWriter().println("</html>");
        }

        else {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (UserInputValidator.isEmailAddressValid(email) && UserInputValidator.isPasswordValid(password)) {
                if (repository.areCredentialsValid(email, password)) {
                    HttpSession newSession = request.getSession();
                    /* assume that the Cookie never expires until the session is invalidated through logout mechanism */
                    // newSession.setMaxInactiveInterval(3600);
                    
                    Authentication.setUserSession(newSession, email);

                    RequestSanitizer.removeAllAttributes(request);
                    request.getRequestDispatcher("home.jsp").forward(request, response);
                } else {
                    RequestSanitizer.removeAllAttributes(request);
                    request.getRequestDispatcher("login.html").forward(request, response);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("<html><head><title>Check input correctness!</title></head>");
                response.getWriter().print("<body>Check input correctness!</body>");
                response.getWriter().println("</html>");
            }
        }
    }
}
