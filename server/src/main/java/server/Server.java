package server;

import server.collection.CityCollectionManager;
import server.exceptions.ServerSocketOpeningException;
import server.io.ClientComManager;
import server.io.ConsoleManager;
import server.io.DataBaseHandler;
import server.utils.CommandManager;
import server.utils.RequestHandler;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Serializable{
    private final int port;
    private RequestHandler requestHandler;
    private ServerSocket serverSocket;
    private final CityCollectionManager collectionManager;
    private String SAVE_PATH = "save.json";
    private final Scanner scanner = new Scanner(System.in);
    private final ExecutorService cachedThreadPool;
    private final CommandManager commandManager;

    public Server(int port, CityCollectionManager collectionManager, DataBaseHandler dataBaseHandler, CommandManager commandManager){
        this.port = port;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        cachedThreadPool = Executors.newCachedThreadPool();
        requestHandler = new RequestHandler(commandManager, dataBaseHandler);
    }

    public void run(){
        try {
            openServerSocket();
            boolean continueRunning = true;

            Runnable userInput = () -> {
                while(true) {
                    String userCommand = scanner.nextLine().trim();

                    if (userCommand.equals("exit")) {
                        System.out.println("Завершение работы сервера");
                        exit();
                    }
                }
            };

            Thread userInputThread = new Thread(userInput);
            userInputThread.start();

            while (true){
                try {
                    Socket clientSocket = serverSocket.accept();
                    cachedThreadPool.submit(new ClientComManager(clientSocket, this, requestHandler));
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.err.println("Не удалось установить соединение с клиентом.");
                }

            }
        }
        catch (ServerSocketOpeningException e){
            ConsoleManager.printErr("Не удалось запустить сервер");
        }
    }

    private void openServerSocket() throws ServerSocketOpeningException {
        try{
            ConsoleManager.print("Запуск сервера...");

            serverSocket = new ServerSocket(port);

            ConsoleManager.print("Сервер успешно запущен");
        }
        catch (IOException e){
            ConsoleManager.printErr("Не удалось запустить сервер с использованием порта " + port);
            throw new ServerSocketOpeningException();
        }
        catch (IllegalArgumentException e){
            ConsoleManager.printErr("Значение порта " + port + "находится за пределами допустимых значений(0, 65535)");
            throw new ServerSocketOpeningException();
        }
    }

    public void exit(){
        collectionManager.save(SAVE_PATH);
        System.exit(0);
    }
}
