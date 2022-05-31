package it.unibz.examproject.servlets;

import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;



    /**
     * @see HttpServlet#HttpServlet()
     */
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);

        if (Authentication.isUserLogged(session)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("<html><head><title>User already logged in!</title></head>");
            response.getWriter().print("<body>User already logged in!</body>");
            response.getWriter().println("</html>");
        } else {

            String name = request.getParameter("name");
            String surname = request.getParameter("surname");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            RequestSanitizer.removeAllAttributes(request);

            if (UserInputValidator.isNameValid(name) && UserInputValidator.isSurnameValid(surname)
                    && UserInputValidator.isEmailAddressValid(email) && UserInputValidator.isPasswordValid(password)) {
                boolean emailAlreadyInUse = repository.emailAlreadyInUse(email);

                if (emailAlreadyInUse) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("<html><head><title>Email already in use!</title></head>");
                    response.getWriter().print("<body>Email already in use!</body>");
                    response.getWriter().println("</html>");
                } else {

                    HttpSession newSession = request.getSession();
                    Authentication.setUserSession(newSession, email);

                    repository.registerNewUser(name, surname, email, password);
                    request.getRequestDispatcher("home.jsp").forward(request, response);
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
