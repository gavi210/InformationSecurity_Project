package it.unibz.examproject.util;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class Authentication {
    private static String SESSION_ATTR_NAME = "user";
    private static String USER_EMAIL = "email";

    public static boolean isUserLogged(HttpSession currentSession) {
        return currentSession != null && currentSession.getAttribute("user") != null;
    }

    public static void setUserSession(HttpSession currentSession, String userEmail) {
        /**
         * put user information - proves that it has logged in
         */
        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("email", userEmail);
        currentSession.setAttribute("user", userInfo);
    }

    public static void removeUserSession(HttpSession currentSession) {
        if(isUserLogged(currentSession))
            currentSession.removeAttribute(SESSION_ATTR_NAME);
    }
}