package it.unibz.examproject.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.List;

public class RequestSanitizer {
    public static void removeAttributesApartFrom(HttpServletRequest request, List<String> toBeKept) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        String nextName;
        while(attributeNames.hasMoreElements()) {
            nextName = attributeNames.nextElement();
            if (!toBeKept.contains(nextName))
                request.removeAttribute(nextName);
        }
    }

    public static void removeAllAttributes(HttpServletRequest request) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        String nextName;
        while(attributeNames.hasMoreElements()) {
            nextName = attributeNames.nextElement();
            request.removeAttribute(nextName);
        }
    }

    public static void keepOnlyEmailAttribute(HttpServletRequest request) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        String nextName;
        while(attributeNames.hasMoreElements()) {
            nextName = attributeNames.nextElement();
            if (!"email".equals(nextName))
                request.removeAttribute(nextName);
        }
    }
}
