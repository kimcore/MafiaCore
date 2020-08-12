package com.github.kimcore.mafia;

public class KoreanUtil {
    public static String get(String name, String firstValue, String secondValue) {
        char lastName = name.charAt(name.length() - 1);
        if (lastName < 0xAC00 || lastName > 0xD7A3) {
            return name;
        }
        String value = (lastName - 0xAC00) % 28 > 0 ? firstValue : secondValue;
        return name + value;
    }
}