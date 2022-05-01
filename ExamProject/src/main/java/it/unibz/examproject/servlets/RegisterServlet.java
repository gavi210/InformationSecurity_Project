package it.unibz.examproject.servlets;

import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import it.unibz.examproject.util.inputvalidation.EmailAddressValidator;
import it.unibz.examproject.util.inputvalidation.NameSurnameValidator;
import it.unibz.examproject.util.inputvalidation.PasswordValidator;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
    
    public void init() throws ServletException {
		try {
			Properties configProperties = new Properties();
			configProperties.load(getServletContext().getResourceAsStream("/dbConfig.properties"));

			String dbms = configProperties.getProperty("db.dbms");
			if("postgres".equals(dbms))
				repository = new PostgresRepository();
			else
				repository = new SQLServerRepository();

			repository.init(configProperties);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		HttpSession session = request.getSession();

		if (Authentication.isUserLogged(session)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("<html><head><title>User already logged in!</title></head>");
			response.getWriter().print("<body>User already logged in!</body>");
			response.getWriter().println("</html>");
		}
		else {
			/**
			 * sanitize the user data both when received and when shown. Ensure and avoids problems of corruption in between.
			 */
			String name = request.getParameter("name"); // since parametrized query, replacement is not needed anymore
			String surname = request.getParameter("surname");
			String email = request.getParameter("email");
			String pwd = request.getParameter("password");

			// once the attributes are loaded, then removed from the object, so to avoid security problems
			RequestSanitizer.removeAllAttributes(request);

			NameSurnameValidator nameValidator = new NameSurnameValidator(name);
			NameSurnameValidator surnameValidator = new NameSurnameValidator(surname);
			EmailAddressValidator emailAddressValidator = new EmailAddressValidator(email);
			PasswordValidator passwordValidator = new PasswordValidator(pwd);

			if(nameValidator.isValid() && surnameValidator.isValid() && emailAddressValidator.isValid() && passwordValidator.isValid()) {
				boolean emailAlreadyInUse = repository.emailAlreadyInUse(email);

				if (emailAlreadyInUse) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print("<html><head><title>Email already in use!</title></head>");
					response.getWriter().print("<body>Email already in use!</body>");
					response.getWriter().println("</html>");

					/**
					 * maybe needed to remove the password parameter from the response page. Just check if it is there
					 */
				} else {
					repository.registerNewUser(name, surname, email, pwd);

					Authentication.setUserSession(session, email);

					request.getRequestDispatcher("home.jsp").forward(request, response);
				}
			}
			else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print("<html><head><title>Check input correctness!</title></head>");
				response.getWriter().print("<body>Check input correctness!</body>");
				response.getWriter().println("</html>");
			}
		}
	}

}
