package com.example.go4lunch.util;

/**
 * Utility class for formatting strings.
 */
public class FormatString {
    /**
     * Capitalizes the first letter of every word in a given text while converting the rest to lowercase.
     *
     * @param text The input text to be formatted.
     * @return The input text with the first letter of each word capitalized and the rest in lowercase.
     */
    public static String capitalizeEveryWord(String text){
        String c = (text != null)? text.trim() : "";
        String[] words = c.split(" ");
        StringBuilder result = new StringBuilder();
        for(String w : words){
            result.append(w.length() > 1 ? w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase() : w).append(" ");
        }
        return result.toString().trim();
    }
}
