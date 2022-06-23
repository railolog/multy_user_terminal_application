package server.io;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleManager {
    public static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    public static void printErr(String err){
        System.out.println("Ошибка: " + err);
    }

    public static void print(String msg){
        System.out.println(msg);
    }

    public static boolean hasNext(){
        try {
            return console.ready();
        }
        catch (IOException e) {
            printErr("Произошла ошибка I/O с консоли");
            return false;
        }
    }

    public static String readLine(){
        try {
            return console.readLine().trim();
        }
        catch (EOFException e) {
            return null;
        }
        catch (IOException e){
            printErr("Произошла ошибка I/O с консоли");
            return "exit";
        }
    }

    public static String readPath() {
        print("Попробуйте ввести путь до другого файла (пустую строку чтобы выйти): ");

        return readLine();
    }
}
