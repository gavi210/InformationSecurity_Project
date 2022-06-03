package it.unibz.examproject.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServletUtil {

    public static boolean isGetMailModeValid(String mode) {
        return Arrays.stream(new String[]{"sent", "inbox"}).sequential().anyMatch(mode::equals); }
}
