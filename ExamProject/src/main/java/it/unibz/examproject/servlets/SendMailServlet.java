package it.unibz.examproject.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.unibz.examproject.model.EmailFromSendMailRequest;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.JsonOperations;
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
import java.util.stream.Collectors;

@WebServlet("/SendMailServlet")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Repository repository;

    public SendMailServlet() {}

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);

		if(!Authentication.isUserLogged(session)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		else {
			Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
			String sender = userInfo.get("email");

			try {
				String requestBody = request.getReader().lines().collect(Collectors.joining(""));
				EmailFromSendMailRequest mail = JsonOperations.getObject(requestBody, EmailFromSendMailRequest.class);

				RequestSanitizer.removeAllAttributes(request);

				if(UserInputValidator.isEmailAddressValid(mail.getReceiver()) && UserInputValidator.isMailSubjectValid(mail.getSubject())
						&& UserInputValidator.isMailBodyValid(mail.getBody()))
					repository.sendNewMail(sender, mail.getReceiver(), mail.getSubject(), mail.getBody(),
							new Date(System.currentTimeMillis()).toInstant().toString());

				else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Check input correctness");
				}
			} catch(JsonProcessingException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			}

		}
	}
}
