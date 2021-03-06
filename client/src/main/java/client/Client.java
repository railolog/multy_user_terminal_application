package client;

import client.exceptions.ConnectToServerException;
import client.io.ConsoleIOManager;
import client.utils.CommandWrapper;
import client.utils.ResponseHandler;
import interaction.Request;
import interaction.Response;
import interaction.ResponseStatus;
import interaction.UserInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class Client {
    private final int BUFFER_SIZE = 128;

    private final ByteBuffer inputBuffer;
    private final int port;
    private int reconnectionAttempts;
    private final int recconectionAttemptsLimit;
    private final int reconnectionTimeout;
    private ResponseHandler responseHandler;
    private Selector selector;
    private SocketChannel socketChannel;
    private boolean logged_in = false;
    private String username;
    private String password;

    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream objectOutputStream;

    public Client(int port, int reconnectionTimeout, int recconectionAttemptsLimit) {
        this.port = port;
        this.reconnectionAttempts = 0;
        this.reconnectionTimeout = reconnectionTimeout;
        this.recconectionAttemptsLimit = recconectionAttemptsLimit;

        inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        }
        catch (IOException e){
            ConsoleIOManager.printErr(e.getMessage());
            System.exit(777);
        }
    }

    public void run(){
        boolean continueRunning = true;

        while (continueRunning) {
            try {
                connectToServer();
                continueRunning = listenServer();
            } catch (ConnectToServerException e) {
                if (reconnectionAttempts >= recconectionAttemptsLimit){
                    ConsoleIOManager.printErr("?????????????????? ??????-???? ?????????????? ?????????????????????? ?? ??????????????");
                    break;
                }
                try {
                    Thread.sleep(reconnectionTimeout);
                }
                catch (IllegalArgumentException e2){
                    ConsoleIOManager.printErr("???????????????????????? ?????????? ????????????????: " + reconnectionTimeout);
                    break;
                }
                catch (InterruptedException e2){
                    ConsoleIOManager.printErr(e.getMessage());
                    break;
                }
            }
            reconnectionAttempts++;
        }
    }

    private boolean listenServer(){
        Request request = null;
        Response response = null;

        String helpMenu = "?????????????????? ??????????????:\nsign_in: ?????????? ?? ???????????????????????? ??????????????\nsign_up: ?????????????? ?????????? ??????????????\nhelp: ?????????????? ???????? ????????????";

        if (!logged_in) {
            System.out.println("?????? ???????????? ???????????? ?? ???????????????????? ???????????????????? ????????????????????????????");
            System.out.println(helpMenu);
        }

        while (!logged_in) {
            CommandWrapper cmd = ConsoleIOManager.readCommand();

            switch (cmd.getCommand()){
                case ("help"):
                    System.out.println(helpMenu);
                    break;
                case ("exit"):
                    return false;
                case ("sign_in"):
                    System.out.print("?????????????? ??????????: ");
                    username = ConsoleIOManager.readLine();
                    System.out.print("?????????????? ????????????: ");
                    password = ConsoleIOManager.readPassword();
                    try {
                        sendRequest(new Request("sign_in", new UserInfo(username, password)));
                        response = getResponse();
                        System.out.println(response.getResponseBody());
                        if (response.getResponseStatus() == ResponseStatus.SUCCESS){
                            logged_in = true;
                            break;
                        }
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        System.err.println("?????????????????? ???????????? ?????? ?????????????????? ?? ????????????????");
                        return true;
                    }
                    break;
                case ("sign_up"):
                    System.out.print("?????????????? ??????????: ");
                    username = ConsoleIOManager.readLine();
                    System.out.print("?????????????? ????????????: ");
                    password = ConsoleIOManager.readPassword();
                    try {
                        sendRequest(new Request("sign_up", new UserInfo(username, password)));
                        response = getResponse();
                        System.out.println(response.getResponseBody());
                    }
                    catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        System.err.println("?????????????????? ???????????? ?????? ?????????????????? ?? ????????????????");
                        return true;
                    }
                    break;
                default:
                    System.out.println(helpMenu);
            }
        }

        response = null;
        responseHandler = new ResponseHandler(new UserInfo(username, password));
        do {
            request = responseHandler.handleResponse(response);

            try {
                sendRequest(request);
                response = getResponse();
            }
            catch (ClassNotFoundException e){
                ConsoleIOManager.printErr("???????????????????? ?????????? ???? ??????????????");
            }
            catch (IOException e){
                ConsoleIOManager.printErr("?????????????????? ???????????? ?????? ?????????????????? ?? ????????????????");
                return true;
            }
        } while (!request.getCommandName().equals("exit"));
        logged_in = false;
        System.out.println("???? ?????????? ???? ???????????? ????????????????");
        return true;
    }

    private void sendRequest(Request request) throws IOException {
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                SocketChannel client = (SocketChannel) key.channel();

                if (key.isWritable()){
                    objectOutputStream.writeObject(request);
                    objectOutputStream.flush();

                    client.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));

                    byteArrayOutputStream.reset();

                    return;
                }
            }
        }
    }

    private Response getResponse() throws IOException, ClassNotFoundException {
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                SocketChannel client = (SocketChannel) key.channel();

                if (key.isReadable()){
                    ArrayList<Byte> byteList = new ArrayList<Byte>();

                    inputBuffer.clear();

                    while (client.read(inputBuffer) > 0){
                        inputBuffer.flip();

                        while (inputBuffer.hasRemaining()){
                            byteList.add(inputBuffer.get());
                        }

                        inputBuffer.clear();
                    }

                    byte[] bytes = new byte[byteList.size()];

                    for (int i = 0; i < byteList.size(); i++){
                        bytes[i] = byteList.get(i);
                    }

                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

                    Response response = (Response) objectInputStream.readObject();

                    objectInputStream.close();

                    return response;
                }
            }
        }
    }

    private void connectToServer() throws ConnectToServerException {
        if (reconnectionAttempts >= 1){
            ConsoleIOManager.println("?????????????????? ?????????????????????? ?? ??????????????");
        }
        else {
            ConsoleIOManager.println("?????????????????????? ?? ??????????????...");
        }
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", port));
            socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            }
            catch (IOException e){
                ConsoleIOManager.printErr(e.getMessage());
                System.exit(777);
            }

            while (!socketChannel.finishConnect()){
                Thread.sleep(500);
            }

            ConsoleIOManager.println("?????????????????????? ?????????????? ??????????????????");
        }
        catch (IOException | InterruptedException e){
            throw new ConnectToServerException();
        }
    }
}
