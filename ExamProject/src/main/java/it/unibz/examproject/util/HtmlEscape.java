package it.unibz.examproject.util;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlEscape {

    public static String escape(String input) {
        if(input == null)
            throw new RuntimeException("Invalid Input: null string");
        return StringEscapeUtils.escapeHtml4(input);
    }
}
