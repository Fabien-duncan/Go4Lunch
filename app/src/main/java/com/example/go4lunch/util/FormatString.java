package com.example.go4lunch.util;

import java.util.Locale;

public class FormatString {
    public static String capitalizeEveryWord(String text){
        String c = (text != null)? text.trim() : "";
        String[] words = c.split(" ");
        String result = "";
        for(String w : words){
            result += (w.length() > 1? w.substring(0, 1).toUpperCase() + w.substring(1, w.length()).toLowerCase() : w) + " ";
        }
        return result.trim();
    }
}
