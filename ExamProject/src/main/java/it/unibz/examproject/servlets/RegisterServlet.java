package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			response.getWriter().println("</html>");
		}
		else {
			// The replacement escapes apostrophe special character in order to store it in SQL

			/**
			 * maybe include the preprocessing part to any input: being stored in the database, and therefore, apply all validations and cleanings
			 */

			// introduce some validation for the input parameters.
			String name = request.getParameter("name").replace("'", "''");
			String surname = request.getParameter("surname").replace("'", "''");;
			String email = request.getParameter("email").replace("'", "''");;
			String pwd = request.getParameter("password").replace("'", "''");;

			/**
			 * sql injection
			 */
			try (Statement st = conn.createStatement()) {
				ResultSet sqlRes = st.executeQuery(
						"SELECT * "
								+ "FROM [user] "
								+ "WHERE email='" + email + "'"
				);

				if (sqlRes.next()) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().print("<html><head><title>Email already in use!</title></head>");
					response.getWriter().print("<body>Email already in use!</body>");
					response.getWriter().println("</html>");
				} else {
					// add new account to database
					st.execute(
							"INSERT INTO [user] ( name, surname, email, password ) "
									+ "VALUES ( '" + name + "', '" + surname + "', '" + email + "', '" + pwd + "' )"
					);

					Authentication.setUserSession(session, email);

					RequestSanitizer.removeAllAttributes(request);

					request.getRequestDispatcher("home.jsp").forward(request, response);
				}

			} catch (SQLException e) {
				e.printStackTrace();
				request.getRequestDispatcher("register.html").forward(request, response);
			}
		}
	}

}
