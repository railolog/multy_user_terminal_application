package client.io;

import client.utils.CommandWrapper;
import core.City;
import core.Coordinates;
import core.Government;
import core.Human;
import exceptions2.CoordinatesException;
import exceptions2.HumanException;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class ConsoleIOManager {
    private static Scanner sc = new Scanner(System.in);
    private static boolean fileMode = false;

    /**
     * Стэк читаемых файлов
     */
    private static Stack<String> fileNamesStack = new Stack<>();

    /**
     * Стэк потоков ввода
     */
    private static Stack<Scanner> scannersStack = new Stack<>();

    public static void println(String msg){
        System.out.println(msg);
    }

    public static void print(String msg){
        System.out.print(msg);
    }

    public static CommandWrapper readCommand() {
        String cmd = "";

        while(cmd.length() == 0) {
            cmd = readLine();
        }

        if (cmd.contains(" ")){
            String[] arr = cmd.split(" ", 2);
            return new CommandWrapper(arr[0].trim(), arr[1].trim());
        }

        return new CommandWrapper(cmd);
    }

    public static City readCity(){
        String name = readName();
        Coordinates coords = readCoordinates();
        Double area = readArea();
        Integer population = readPopulation();
        Long meters = readMetersAboveSeaLevel();
        boolean capital = readCapital();
        int telephoneCode = readTelephoneCode();
        Government gov = readGovernment();
        Human governor = readGovernor();

        return new City(name, coords, area, population, meters, capital, telephoneCode, gov, governor);
    }

    private static String readName(){
        print("Введите название города(поле не может быть пустым): ");
        String name = readLine();

        if(name.length() == 0){
            printErr("поле не может быть пустым");
            return fileMode ? null : readName();
        }
        return name;
    }

    private static Coordinates readCoordinates(){
        try {
            return new Coordinates(readXCoord(), readYCoord());
        }
        catch (CoordinatesException e){
            printErr(e.getMessage());
            return fileMode ? null : readCoordinates();
        }
    }

    private static Float readXCoord(){
        print("Введите координату X: ");

        String line = readLine();
        if (line.length() == 0){
            printErr("Введена пустая строка");
            return fileMode ? null : readXCoord();
        }
        try {
            return Float.parseFloat(line);
        }
        catch (NumberFormatException e){
            printErr("Вероятно введено не число");
            return fileMode ? null : readXCoord();
        }
    }

    private static Float readYCoord(){
        print("Введите координату Y: ");

        String line = readLine();
        if (line.length() == 0){
            printErr("Введена пустая строка");
            return fileMode ? null : readYCoord();
        }
        try {
            return Float.parseFloat(line);
        }
        catch (NumberFormatException e){
            printErr("Вероятно введено не число");
            return fileMode ? null : readYCoord();
        }
    }

    private static Double readArea(){
        print("Введите площадь(> 0, поле не может быть пустым): ");
        double area;

        try {
            area = Double.parseDouble(readLine());
        }
        catch (NumberFormatException e){                // пустой ввод
            printErr("вероятно, введено не число");
            return fileMode ? null : readArea();
        }

        if (area <= 0){
            printErr("вы ввели значение не больше нуля");
            return fileMode ? null : readArea();
        }
        return area;
    }

    private static Integer readPopulation(){
        print("Введите кол-во жителей: ");
        int pop;

        try{
            pop = Integer.parseInt(readLine());
        }
        catch (NumberFormatException e){
            if (e.getMessage().equals("empty String")){
                printErr("поле не может быть пустым");    // не работает с целыми числами
                return fileMode ? null : readPopulation();
            }
            printErr("вероятно, введено не целое число");
            return fileMode ? null : readPopulation();
        }

        if (pop <= 0){
            printErr("вы ввели значение не больше нуля");
            return fileMode ? null : readPopulation();
        }
        return pop;
    }

    private static Long readMetersAboveSeaLevel(){
        print("Введите высоту над уровнем моря(целое число): ");
        long meters;
        String s = readLine();

        if (s.length() == 0){
            return null;
        }

        try{
            meters = Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            printErr("введено не целое число");
            return fileMode ? null : readMetersAboveSeaLevel();
        }

        return meters;
    }

    private static boolean readCapital(){
        print("Город - столица? (Y/N, 1/0, Yes/No, Д/Н, Да/Нет): ");
        String ans = readLine().toLowerCase();

        String[] yes = new String[] {"y", "1", "yes", "д", "да"};    // make repeation
        String[] no = new String[] {"n", "0", "no", "н", "нет"};

        if (Arrays.asList(yes).contains(ans)){
            return true;
        }
        else if (Arrays.asList(no).contains(ans)){
            return false;
        }
        return false;
    }

    private static int readTelephoneCode(){
        print("Введите телефонный код: ");
        int code;

        try{
            code = Integer.parseInt(readLine());
        }
        catch (NumberFormatException e){
            if (e.getMessage().equals("empty String")){
                printErr("поле не может быть пустым");
            }
            else {
                printErr("введено не число");
            }
            return fileMode ? -1 : readTelephoneCode();
        }

        if (code > 100000 || code <= 0){
            printErr("Введено значение не из промежутка [1; 100000]");
            return fileMode ? -1 : readTelephoneCode();
        }

        return code;
    }

    private static Government readGovernment(){
        Government[] governments = Government.values();

        println("Введите тип правления или пропустите");     //пропуск не работает
        int a = 1, b = governments.length;

        for (int i = 0; i < b; i++){
            println(governments[i] + ": " + (i + 1));
        }

        String s = readLine();

        if (s.length() == 0){
            return null;
        }

        int input;
        try{
            input = Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            printErr("Введно не целое число");
            return fileMode ? null : readGovernment();
        }

        if (input > b || input < a){
            printErr("Введено число не из промежутка");
            return fileMode ? null : readGovernment();
        }

        return governments[input - 1];
    }

    private static Human readGovernor(){
        print("Введите рост правителя: ");
        double height;
        String s = readLine();

        if (s.length() == 0){
            return null;
        }

        try {
            height = Double.parseDouble(s);
        }
        catch (NumberFormatException e){
            printErr("Введено не вещественное число");
            return fileMode ? null : readGovernor();
        }

        try {
            return new Human(height);
        }
        catch (HumanException e){
            printErr(e.getMessage());
            return fileMode ? null : readGovernor();
        }
    }

    public static void setFileInput(String path){
        if (fileNamesStack.empty() || fileNamesStack.search(path) == -1){
            try {
                Scanner newScanner = new Scanner(new File(path));
                setScanner(newScanner);
                fileNamesStack.push(path);
                scannersStack.push(newScanner);
                fileMode = true;
            }
            catch (FileNotFoundException e){
                printErr("нет доступа к файлу");
            }
        }
        else {
            printErr("Рекурсивный запуск скрипта");
        }
    }

    public static void setScanner(Scanner source){
        sc = source;
    }

    public static void setScanner(InputStream source){
        sc = new Scanner(source);
    }

    public static void printErr(String err){
        System.out.println("Ошибка: " + err);
    }

    public static String readLine(){
        try {
            return sc.nextLine().trim();
        }
        catch (NoSuchElementException e){
            setPreviousScanner();
            return readLine();
        }
    }

    public static String readPassword(){
        String password;
        Console console = System.console();
        if (console != null){
            char[] symbols = console.readPassword();
            password = String.valueOf(symbols).trim();
        }
        else {
            password = readLine();
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1, digest);
            password = bigInteger.toString(16);

            while (password.length() < 32){
                password = "0" + password;
            }

            return password;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setPreviousScanner() {
        fileNamesStack.pop();
        scannersStack.pop();

        if (fileNamesStack.empty()){
            setScanner(System.in);
            fileMode = false;
        }
        else {
            setScanner(scannersStack.peek());
        }
    }
}
