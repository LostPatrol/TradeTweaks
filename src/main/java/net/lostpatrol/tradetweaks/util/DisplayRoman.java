package net.lostpatrol.tradetweaks.util;

public class DisplayRoman {
    private static final String[] ROMAN_1_TO_20 = {
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
            "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"
    };
    private static final int MAX_ROMAN_LEVEL = 255;

    public static String toRoman(int num) {
        if (num >= 1 && num <= ROMAN_1_TO_20.length) {
            return ROMAN_1_TO_20[num - 1];
        }
        if (num > ROMAN_1_TO_20.length && num <= MAX_ROMAN_LEVEL) {
            return dynamicRoman(num);
        }
        return String.valueOf(num);
    }

    private static String dynamicRoman(int num) {
        final int[] values = {100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length && num > 0; i++) {
            while (num >= values[i]) {
                sb.append(symbols[i]);
                num -= values[i];
            }
        }
        return sb.toString();
    }
}
