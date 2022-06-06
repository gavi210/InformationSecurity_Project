package it.unibz.examproject.servlets;

import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet("/ResetDatabaseServlet")
public class ResetDatabaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Repository repository;

    public ResetDatabaseServlet() {
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
        try {
            repository.resetDatabase();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
