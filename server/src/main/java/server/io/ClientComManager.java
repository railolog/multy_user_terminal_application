package server.io;

import core.City;
import interaction.Request;
import interaction.Response;
import interaction.ResponseStatus;
import server.Server;
import server.utils.RequestHandler;

import java.io.*;
import java.net.Socket;

public class ClientComManager implements AutoCloseable, Runnable{
    private final ObjectInputStream objectInputStream;
    private final OutputStream out;
    private final Server server;
    private boolean loggedIn = false;
    private final RequestHandler requestHandler;

    public ClientComManager(Socket socket, Server server, RequestHandler requestHandler) throws IOException {
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        out = socket.getOutputStream();
        this.server = server;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        Request userRequest;
        Response serverResponse = null;

        try {
            do {
                userRequest = getRequest();
                serverResponse = requestHandler.handleRequest(userRequest, this);
                sendResponse(serverResponse);
            } while (serverResponse.getResponseStatus() != ResponseStatus.CLIENT_EXIT);
        }
        catch (ClassNotFoundException e){
            System.err.println("Некорректный запрос от клиента");
        }
        catch (IOException e){
            System.err.println("Клиент отсоединился");
        }
    }

    public Request getRequest() throws IOException, ClassNotFoundException {
        return (Request) objectInputStream.readObject();
    }

    public void sendResponse(Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(response);
        objectOutputStream.flush();

        out.write(byteArrayOutputStream.toByteArray());
        out.flush();

        objectOutputStream.close();
        byteArrayOutputStream.close();
    }

    public City readCity() throws IOException, ClassNotFoundException {
        sendResponse(new Response(ResponseStatus.READ_ELEM));
        Request request = getRequest();

        System.out.println("Считали"); //FIXME

        if (request.getCommandObjectArgument() != null){
            return (City) request.getCommandObjectArgument();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        objectInputStream.close();
        out.close();
    }
}
