package com.suslov.jetbrains.util;

/**
 * @author Mikhail Suslov
 */
public class ConsoleUtil {
    public static final String COMP_WIN = "Sorry, but the computer chose %s";
    public static final String USER_WIN = "Well done. The computer chose %s and failed";
    public static final String IS_DRAW = "There is a draw (%s)";
    public static final String INPUT_ERROR = "Invalid input";

    public static void displayMessage(String message) {
        System.out.println(message);
    }
}
