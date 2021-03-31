package io.github.seggan.sfcalc;

public final class Util {

    private Util() {}

    static String format(String s, String i, String j) {
        return s.replaceFirst("%s", i).replaceFirst("%s", j);
    }

    static String format(String s, long a, String i) {
        return s.replace("%d", Long.toString(a)).replace("%s", i);
    }

    static String format(String s, long a, long b, long c, long d) {
        return s.replaceFirst("%d", Long.toString(a)).replaceFirst("%d", Long.toString(b)).replaceFirst("%d", Long.toString(c)).replaceFirst("%d", Long.toString(d));
    }

    static int getSlots(int c) {
        int n = 9;
        while (n < c) {
            n *= 2;
        }
        return n;
    }
}
