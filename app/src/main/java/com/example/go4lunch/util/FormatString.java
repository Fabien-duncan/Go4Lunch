package com.example.go4lunch.util;


public class FormatString {
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
