package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		
		String email = request.getParameter("email").replace("'", "''");;
		String pwd = request.getParameter("password").replace("'", "''");;

		/**
		 * sql injection: validate inputs
		 */
		try (Statement st = conn.createStatement()) {
			ResultSet sqlRes = st.executeQuery(
				"SELECT * "
				+ "FROM [user] "
				+ "WHERE email='" + email + "' "
					+ "AND password='" + pwd + "'"
			);
			
			if (sqlRes.next()) {
				// 3: column index for the email in the table
				request.setAttribute("email", sqlRes.getString(3));

				// 4: column index for the password in the table - ? why should it be returned the user? it has already prompted the password and knows it
				request.setAttribute("password", sqlRes.getString(4));

				//System.out.println(String.format("User: %s login in successfully!", request.getParameter("email")));

				request.setAttribute("content", "");

				// populate the response with attributes email and password, then forward the main page.
				request.getRequestDispatcher("home.jsp").forward(request, response);
				
				
			} else {
				// System.out.println("Login failed!");
				// ask again the user to log in
				request.getRequestDispatcher("login.html").forward(request, response);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			request.getRequestDispatcher("login.html").forward(request, response);
		}
	}
}
