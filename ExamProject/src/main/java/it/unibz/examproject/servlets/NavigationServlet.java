package it.unibz.examproject.servlets;

import it.unibz.examproject.util.HtmlEscape;
import it.unibz.examproject.util.RequestSanitizer;
import it.unibz.examproject.model.Email;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import it.unibz.examproject.util.Authentication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class NavigationServlet
 */
@WebServlet("/NavigationServlet")
public class NavigationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Repository repository;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NavigationServlet() {
        super();
    }

    public void init() {
		try {
			Properties configProperties = new Properties();
			configProperties.load(getServletContext().getResourceAsStream("/dbConfig.properties"));

			String dbms = configProperties.getProperty("db.dbms");
			if("postgres".equals(dbms))
				repository = new PostgresRepository();
			else
				repository = new SQLServerRepository();

			repository.init(configProperties);

		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);

		if(!Authentication.isUserLogged(session)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("<html><head><title>Login first!</title></head>");
			response.getWriter().print("<body>Login first!</body>");
			response.getWriter().println("</html>");
		}
		else {
			Map<String,String> userInfo = (Map<String, String>) session.getAttribute("user");
			String email = userInfo.get("email");

			// dynamically change the web page
			if (request.getParameter("userAction") != null) {
				String action = request.getParameter("userAction");
				switch (action) {
					case "NEW_EMAIL":
						request.setAttribute("content", getHtmlForNewMail());
						break;
					case "INBOX":
						request.setAttribute("content", getHtmlForInbox(email));
						break;
					case "SENT":
						request.setAttribute("content", getHtmlForSent(email));
						break;
				}
			}
			else
				request.removeAttribute("content");

			List<String> toBeKept = new ArrayList<>();
			toBeKept.add("content");
 			RequestSanitizer.removeAttributesApartFrom(request, toBeKept);

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
		List<Email> inbox = repository.getReceivedEmails(email);

		StringBuilder output = new StringBuilder();
		output.append("<div>\r\n");

		inbox.forEach(mail -> {

			String escapedEmail = HtmlEscape.escape(mail.getSender());
			String escapedTimestamp = HtmlEscape.escape(mail.getTimestamp());
			String escapedSubject = HtmlEscape.escape(mail.getSubject());
			String escapedBody = HtmlEscape.escape(mail.getBody());

			output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
			output.append("FROM:&emsp;").append(escapedEmail).append("&emsp;&emsp;AT:&emsp;").append(escapedTimestamp);
			output.append("</span>");
			output.append("<br><b>").append(escapedSubject).append("</b>\r\n");
			output.append("<br>").append(escapedBody);
			output.append("</div>\r\n");

			output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
		});

		output.append("</div>");

		return output.toString();
	}

	/**
	 * HTML code returned the user. Here should be placed the encryption process by the user with the public key of the client.
	 * Servlet should provide back the code to encrypt the input. With the encrypted input, send the information over the network with the
	 * doPost request.
	 * @return
	 */
	private String getHtmlForNewMail() {
		return
			"<form id=\"submitForm\" class=\"form-resize\" action=\"SendMailServlet\" method=\"post\">\r\n"
			+ "		<input class=\"single-row-input\" type=\"email\" name=\"receiver\" placeholder=\"Receiver\" required>\r\n"
			+ "		<input class=\"single-row-input\" type=\"text\"  name=\"subject\" placeholder=\"Subject\" required>\r\n"
			+ "		<textarea class=\"textarea-input\" name=\"body\" placeholder=\"Body\" wrap=\"hard\" required></textarea>\r\n"
			+ "		<input type=\"submit\" name=\"sent\" value=\"Send\">\r\n"
			+ "	</form>";
	}

	private String getHtmlForSent(String email) {
		List<Email> sent = repository.getSentEmails(email);

		StringBuilder output = new StringBuilder();
		output.append("<div>\r\n");

		sent.forEach(mail -> {

			String escapedReceiver = HtmlEscape.escape(mail.getReceiver());
			String escapedTimestamp = HtmlEscape.escape(mail.getTimestamp());
			String escapedSubject = HtmlEscape.escape(mail.getSubject());
			String escapedBody = HtmlEscape.escape(mail.getBody());

			output.append("<div style=\"white-space: pre-wrap;\"><span style=\"color:grey;\">");
			output.append("TO:&emsp;").append(escapedReceiver).append("&emsp;&emsp;AT:&emsp;").append(escapedTimestamp);
			output.append("</span>");
			output.append("<br><b>").append(escapedSubject).append("</b>\r\n");
			output.append("<br>").append(escapedBody);
			output.append("</div>\r\n");

			output.append("<hr style=\"border-top: 2px solid black;\">\r\n");
		});

		output.append("</div>");

		return output.toString();
	}
}
