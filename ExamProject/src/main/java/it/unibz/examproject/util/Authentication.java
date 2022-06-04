package it.unibz.examproject.util;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class Authentication {
    private static final String SESSION_ATTR_NAME = "user";

    public static boolean isUserLogged(HttpSession currentSession) {
        return currentSession != null && currentSession.getAttribute(SESSION_ATTR_NAME) != null;
    }

    public static void setUserSession(HttpSession currentSession, String userEmail) {
        Map<String,String> userInfo = new HashMap<>();
        String USER_EMAIL = "email";
        userInfo.put(USER_EMAIL, userEmail);
        currentSession.setAttribute(SESSION_ATTR_NAME, userInfo);
    }
}
