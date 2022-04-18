package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import it.unibz.examproject.db.queries.ExistsUserQuery;
import it.unibz.examproject.db.queries.Query;
import it.unibz.examproject.db.queries.RegisterQuery;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import it.unibz.examproject.util.userinput.EmailValidator;
import it.unibz.examproject.util.userinput.NameSurnameValidator;
import it.unibz.examproject.util.userinput.PasswordValidator;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    
	private static Connection conn;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }
    
    public void init() throws ServletException {
		try {
			// configuration put in the web content
			InputStream dbConnectionProperties = getServletContext().getResourceAsStream("/dbConfig.properties");
			conn = DatabaseConnection.initializeDatabase(dbConnectionProperties);

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
			// validate user inputs
			String name = request.getParameter("name"); // since parametrized query, replacement is not needed anymore
			String surname = request.getParameter("surname");
			String email = request.getParameter("email");
			String pwd = request.getParameter("password");

			NameSurnameValidator nameValidator = new NameSurnameValidator(name);
			NameSurnameValidator surnameValidator = new NameSurnameValidator(surname);
			EmailValidator emailValidator = new EmailValidator(email);
			PasswordValidator passwordValidator = new PasswordValidator(pwd);

			if(nameValidator.isValid() && surnameValidator.isValid() && emailValidator.isValid() && passwordValidator.isValid()) {
				try {
					Query existsUserQuery = new ExistsUserQuery(conn, email);
					ResultSet sqlRes = existsUserQuery.executeQuery();

					if (sqlRes.next()) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().print("<html><head><title>Email already in use!</title></head>");
						response.getWriter().print("<body>Email already in use!</body>");
						response.getWriter().println("</html>");
					} else {
						// add new account to database
						Query registrationQuery = new RegisterQuery(conn, name, surname, email, pwd);
						registrationQuery.executeQuery();

						Authentication.setUserSession(session, email);

						RequestSanitizer.removeAllAttributes(request);

						request.getRequestDispatcher("home.jsp").forward(request, response);
					}

				} catch (SQLException e) {
					e.printStackTrace();
					request.getRequestDispatcher("register.html").forward(request, response);
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
