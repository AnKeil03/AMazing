package com.seProject.groupProject7.login;


public class Cryptography {

    private static final String KEY = "23482342234239472349898";


    // basic weak security features; will strengthen with time
    public static String cipher(String text) {
        String key = KEY;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < text.length(); i++)
            sb.append((char)(text.charAt(i) ^ key.charAt(i % key.length())));
        String result = sb.toString();
        return result;
    }


}
