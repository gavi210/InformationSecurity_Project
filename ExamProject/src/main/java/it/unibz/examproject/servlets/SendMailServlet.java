package it.unibz.examproject.servlets;

import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Servlet implementation class SendMailServlet
 */
@WebServlet("/SendMailServlet")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Repository repository;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMailServlet() {}

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

		} catch (ClassNotFoundException | SQLException | IOException e) {
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
			/**
			 * validate user provided information (since could be encrypted (harmless) or plain text: malicious code)
			 */
			/**
			 * sanitize the user data both when received and when shown. Ensure and avoids problems of corruption in between.
			 */
			Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
			String sender = userInfo.get("email");

			String receiver = request.getParameter("receiver");
			String subject = request.getParameter("subject");
			String body = request.getParameter("body");
			String timestamp = new Date(System.currentTimeMillis()).toInstant().toString();

			// attributes removed
			RequestSanitizer.removeAllAttributes(request);

			if(UserInputValidator.isEmailAddressValid(receiver) && UserInputValidator.isMailSubjectValid(subject)
				&& UserInputValidator.isMailBodyValid(body))
				repository.sendNewMail(sender, receiver, subject, body, timestamp);

			else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print("<html><head><title>Check input correctness!</title></head>");
				response.getWriter().print("<body>Check input correctness!</body>");
				response.getWriter().println("</html>");
			}

			RequestSanitizer.removeAllAttributes(request);
			request.getRequestDispatcher("home.jsp").forward(request, response);
		}
	}
}
