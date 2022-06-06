package it.unibz.examproject.util;

import java.util.Arrays;

public class ServletUtil {

    public static boolean isGetMailModeValid(String mode) {
        return Arrays.stream(new String[]{"sent", "inbox"}).sequential().anyMatch(mode::equals); }
}
