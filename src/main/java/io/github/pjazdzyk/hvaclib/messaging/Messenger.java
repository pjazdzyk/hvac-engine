package io.github.pjazdzyk.hvaclib.messaging;

public final class Messenger {

    private Messenger() {}

    public static void print(Object obj) {
        System.out.print(obj);
    }

    public static void printLine(Object obj) {
        System.out.println(obj);
    }

    public static void printf(String formattedText, Object... args) {
        System.out.printf(formattedText, args);
    }

    public static void printEmptyLine() {
        System.out.println();
    }

}
