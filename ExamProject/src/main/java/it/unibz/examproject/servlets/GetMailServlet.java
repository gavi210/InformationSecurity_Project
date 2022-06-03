package it.unibz.examproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.examproject.model.Email;
import it.unibz.examproject.model.Login;
import it.unibz.examproject.util.*;
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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@WebServlet("/GetMailServlet")
public class GetMailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    public GetMailServlet() {
        super();
    }

    public void init() {
        try {
            Properties configProperties = new Properties();
            configProperties.load(getServletContext().getResourceAsStream("/dbConfig.properties"));

            String dbms = configProperties.getProperty("db.dbms");
            if ("postgres".equals(dbms))
                repository = new PostgresRepository();
            else
                repository = new SQLServerRepository();

            repository.init(configProperties);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession(false);

        if(!Authentication.isUserLogged(session)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        else {
            Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
            String userEmail = userInfo.get("email");

            String mode = request.getParameter("mode");
            if(mode == null || !ServletUtil.isGetMailModeValid(mode)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong mode parameter");
            }
            else {
                List<Email> userEmails;
                switch(mode) {
                    case "inbox":
                        userEmails = repository.getReceivedEmails(userEmail);
                        break;
                    case "sent":
                        userEmails = repository.getSentEmails(userEmail);
                        break;
                    default:
                        userEmails = new LinkedList<>();
                }

                String jsonOutput = new ObjectMapper().writeValueAsString(userEmails);
                PrintWriter writer = response.getWriter();
                writer.write(jsonOutput);
                writer.flush();
            }

        }
    }
}
