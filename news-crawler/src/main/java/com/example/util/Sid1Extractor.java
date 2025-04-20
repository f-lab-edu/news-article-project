package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sid1Extractor {
    public static String extractSid1(String url) {
        Pattern pattern = Pattern.compile("sid1=(\\d{3})");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "000";
    }
}
