package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import it.unibz.examproject.db.queries.LoginQuery;
import it.unibz.examproject.db.queries.Query;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Connection conn;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * set up the connection with the database
	 */
    public void init() {
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

	/**
	 * onPost
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		// if user logged in, DROP the request
		HttpSession session = request.getSession();

		if (Authentication.isUserLogged(session)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("<html><head><title>User already logged in!</title></head>");
			response.getWriter().print("<body>User already logged in!</body>");
			response.getWriter().println("</html>");
		}

		// starts the login sequence
		else {
			// later should be validated
			String email = request.getParameter("email").replace("'", "''");;
			String pwd = request.getParameter("password").replace("'", "''");;

			/**
			 * sql injection: validate inputs
			 */
			try {
				Query query = new LoginQuery(conn, email, pwd);

				ResultSet sqlRes = query.executeQuery();

				// valid credentials
				if (sqlRes.next()) {
					Authentication.setUserSession(session, email);
					RequestSanitizer.removeAllAttributes(request);

					request.getRequestDispatcher("home.jsp").forward(request, response);
				} else {

					RequestSanitizer.removeAllAttributes(request);
					request.getRequestDispatcher("login.html").forward(request, response);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				request.getRequestDispatcher("login.html").forward(request, response);
			}
		}
	}
}
