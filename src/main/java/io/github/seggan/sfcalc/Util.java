package io.github.seggan.sfcalc;

public final class Util {
    static String capitalize(String s) {
        StringBuilder capped = new StringBuilder();
        String string = s.trim();

        for (int i = 0; i < string.length(); i++){
            char c = string.charAt(i);

            if (i == 0) {
                capped.append(Character.toUpperCase(c));
                continue;
            }

            c = Character.toLowerCase(c);

            if (string.charAt(i - 1) == ' ') {
                c = Character.toUpperCase(c);
            }

            capped.append(c);
        }

        return capped.toString();
    }

    static String format(String s, int a, String i) {
        return s.replace("%d", Integer.toString(a)).replace("%s", i);
    }

    static int getSlots(int c) {
        int n = 9;
        while (n < c) {
            n *= 2;
        }
        return n;
    }
}
