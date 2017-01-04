package org.itracker.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLUtilities {

    private static final Logger logger = LoggerFactory.getLogger(HTMLUtilities.class);
    private static final Pattern pattern = Pattern.compile("<[\\w/].*?>", Pattern.CASE_INSENSITIVE);
    public static String removeMarkup(String input) {
        String output = (input == null ? "" : input);
        Matcher matcher = pattern.matcher(input);
        if (matcher != null && !output.equals("")) {
            output = matcher.replaceAll("");
        } else {
            logger.debug("Failed removing markup.  Pattern = " + pattern + "   Output = " + output);
        }
        return output;
    }
}
