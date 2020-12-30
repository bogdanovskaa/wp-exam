package mk.ukim.finki.wp.exam.util;

import java.util.Map;

public class SubmissionHelper {

    public static String index;

    public static String session;

    public static void submitSource(Map<String, String> content) {
        System.out.println(content.keySet());
    }

    public static void submitSuccessAssert(String message, Object expected, Object actual) {

    }

    public static void submitFailedAssert(String message, Object expected, Object actual) {

    }




}