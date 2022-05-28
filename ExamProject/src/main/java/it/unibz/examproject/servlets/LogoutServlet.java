package it.unibz.examproject.servlets;

import it.unibz.examproject.util.Authentication;
import it.unibz.examproject.util.RequestSanitizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
    }

    /**
     * onPost
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // getSession(false): it does not create a new session if the current on does not exist
        HttpSession session = request.getSession(false);

        Authentication.removeUserSession(session);

        RequestSanitizer.removeAllAttributes(request);

        request.getRequestDispatcher("login.html").forward(request, response);

    }
}
