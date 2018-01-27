package rs.etf.si3is1.utils;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner INPUT = new Scanner(System.in);
    
    public static void clear() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            for (int i = 0; i < 1000; i++) {
                System.out.println();
            }
        }
    }

    public static void pause() {
        try {
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            System.out.println("Press enter to continue . . . ");
            INPUT.nextLine();
        }
    }

    public static void choice(Runnable[] funcs) {
        while (true) {
            System.out.print("> ");
            int i = INPUT.nextInt();
            if (i >= 0 && i < funcs.length && funcs[i] != null) {
                funcs[i].run();
                return;
            } else {
                System.out.println("Invalid input, try again");
            }
        }
    }
    
    private ConsoleUtils() {}
}
