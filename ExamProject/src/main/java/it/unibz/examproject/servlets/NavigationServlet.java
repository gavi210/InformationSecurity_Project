package it.unibz.examproject.servlets;

import it.unibz.examproject.db.DatabaseConnection;
import it.unibz.examproject.db.queries.Query;
import it.unibz.examproject.db.queries.ReceivedEmailsQuery;
import it.unibz.examproject.db.queries.SentEmailsQuery;
import it.unibz.examproject.util.Authentication;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Safelist;
import org.jsoup.safety.Whitelist;

/**
 * Servlet implementation class NavigationServlet
 */
@WebServlet("/NavigationServlet")
public class NavigationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static Connection conn;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NavigationServlet() {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		// check if the user is logged in
		HttpSession session = request.getSession(false);

		if(!Authentication.isUserLogged(session)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("<html><head><title>Login first!</title></head>");
			response.getWriter().print("<body>Login first!</body>");
			response.getWriter().println("</html>");
		}
		else {
			// if here, the email provided was correct and has already been validated. Trust the user: where problems could arise since trusting.
			Map<String,String> userInfo = (Map<String, String>) session.getAttribute("user");
			String email = userInfo.get("email");

			// change the web page to be shown
			if (request.getParameter("userAction") != null) {
				String action = request.getParameter("userAction");
				if (action.equals("NEW_EMAIL"))
					request.setAttribute("content", getHtmlForNewMail(email));
				else if (action.equals("INBOX"))
					request.setAttribute("content", getHtmlForInbox(email));
				else if (action.equals("SENT"))
					request.setAttribute("content", getHtmlForSent(email));
			}
			else
				request.removeAttribute("content");

			request.getRequestDispatcher("home.jsp").forward(request, response);
		}
	}

	/**
	 * provides the html page for the user to read the received emails
	 * Load the content, decrypt it on the client side with the stored private key
	 * @param email user email
	 * @return
	 */
	private String getHtmlForInbox(String email) {
		try {
			Query getReceivedEmailsQuery = new ReceivedEmailsQuery(conn, email);
			ResultSet sqlRes = getReceivedEmailsQuery.executeQuery();
			
			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");

			// reading the information from the database: should have already been sanitized: avoid cross-site scripting.
			while (sqlRes.next()) {
				String unsafeEmailSubject = sqlRes.getString(3);

				// SafeList.none() only pure text is considered for the application.
				String safeEmailSubject = Jsoup.clean(unsafeEmailSubject, Safelist.none());
				// get user inputs
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				//output.append("FROM:&emsp;" + sqlRes.getString(1) + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
				output.append("FROM:&emsp;" + sqlRes.getString(1) + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
				output.append("</span>");
				output.append("<br><b>" + safeEmailSubject + "</b>\r\n");
				output.append("<br>" + sqlRes.getString(4));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			}
			
			output.append("</div>");

			return output.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}

	/**
	 * HTML code returned the user. Here should be placed the encryption process by the user with the public key of the client.
	 * Servlet should provide back the code to encrypt the input. With the encrypted input, send the information over the network with the
	 * doPost request.
	 * @param email
	 * @return
	 */
	private String getHtmlForNewMail(String email) {
		return 
			"<form id=\"submitForm\" class=\"form-resize\" action=\"SendMailServlet\" method=\"post\">\r\n"
			+ "		<input type=\"hidden\" name=\"email\" value=\""+email+"\">\r\n"
			+ "		<input class=\"single-row-input\" type=\"email\" name=\"receiver\" placeholder=\"Receiver\" required>\r\n"
			+ "		<input class=\"single-row-input\" type=\"text\"  name=\"subject\" placeholder=\"Subject\" required>\r\n"
			+ "		<textarea class=\"textarea-input\" name=\"body\" placeholder=\"Body\" wrap=\"hard\" required></textarea>\r\n"
			+ "		<input type=\"submit\" name=\"sent\" value=\"Send\">\r\n"
			+ "	</form>";
	}
	
	private String getHtmlForSent(String email) {
		try {
			Query sentEmailsQuery = new SentEmailsQuery(conn, email);
			ResultSet sqlRes = sentEmailsQuery.executeQuery();

			StringBuilder output = new StringBuilder();
			output.append("<div>\r\n");
			
			while (sqlRes.next()) {
				output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
				output.append("TO:&emsp;" + sqlRes.getString(2) + "&emsp;&emsp;AT:&emsp;" + sqlRes.getString(5));
				output.append("</span>");
				output.append("<br><b>" + sqlRes.getString(3) + "</b>\r\n");
				output.append("<br>" + sqlRes.getString(4));
				output.append("</div>\r\n");
				
				output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
			}
			
			output.append("</div>");
			
			return output.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR IN FETCHING INBOX MAILS!";
		}
	}
}
