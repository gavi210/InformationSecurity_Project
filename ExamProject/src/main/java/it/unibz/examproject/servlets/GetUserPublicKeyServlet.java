package it.unibz.examproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.examproject.model.Email;
import it.unibz.examproject.model.UserPublicKey;
import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.JsonOperations;
import it.unibz.examproject.util.UserInputValidator;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@WebServlet("/GetUserPublicKeyServlet")
public class GetUserPublicKeyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    public GetUserPublicKeyServlet() {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String targetEmail = request.getParameter("email");

        if(UserInputValidator.isEmailAddressValid(targetEmail)) {
            UserPublicKey publicKey = repository.getUserPublicKey(targetEmail);


            String jsonOutput = new ObjectMapper().writeValueAsString(publicKey);
            PrintWriter writer = response.getWriter();
            writer.write(jsonOutput);
            writer.flush();
            writer.close();
        }
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed request");
        }
    }
}
