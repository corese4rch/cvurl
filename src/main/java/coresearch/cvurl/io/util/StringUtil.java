package coresearch.cvurl.io.util;

public class StringUtil {

    public static String removeOneLeadingSpace(String input) {
        if (input == null)
            return null;

        if (input.startsWith(" "))
            return input.substring(1);

        return input;
    }
}
