package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import it.unibz.examproject.db.queries.InsertNewMailQuery;
import it.unibz.examproject.db.queries.Query;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class SendMailServlet
 */
@WebServlet("/SendMailServlet")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMailServlet() {
        super();
        // TODO Auto-generated constructor stub
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

	/**
	 * how do we ensure that the client submitting the request is the one logged in? authorization and authentication maybe
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		HttpSession session = request.getSession();

		if(!Authentication.isUserLogged(session)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().print("<html><head><title>Login first!</title></head>");
			response.getWriter().print("<body>Login first!</body>");
			response.getWriter().println("</html>");
		}
		else {
			// validate user info
			// retrieved from the current session
			Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
			String sender = userInfo.get("email");

			String receiver = request.getParameter("receiver").replace("'", "''");
			String subject = request.getParameter("subject").replace("'", "''");
			String body = request.getParameter("body").replace("'", "''");
			String timestamp = new Date(System.currentTimeMillis()).toInstant().toString();

			/**
			 * sanitize the user data both when received and when shown. Ensure and avoids problems of corruption in between.
			 */
			try {
				Query insertNewEmail = new InsertNewMailQuery(conn, sender, receiver, subject, body, timestamp);
				insertNewEmail.executeQuery();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			RequestSanitizer.removeAllAttributes(request);
			request.getRequestDispatcher("home.jsp").forward(request, response);
		}
	}
}
