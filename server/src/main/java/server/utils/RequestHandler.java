package server.utils;

import interaction.Request;
import interaction.Response;
import interaction.ResponseStatus;
import interaction.UserInfo;
import server.io.ClientComManager;
import server.io.DataBaseHandler;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class RequestHandler {
    private final CommandManager commandManager;
    private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private final DataBaseHandler dataBaseHandler;
    private Integer id;

    public RequestHandler(CommandManager commandManager, DataBaseHandler dataBaseHandler) {
        this.commandManager = commandManager;
        this.dataBaseHandler = dataBaseHandler;
    }

    public Response handleRequest(Request request, ClientComManager clientComManager) throws IOException, ClassNotFoundException {
        RequestExecutor requestExecutor = new RequestExecutor(request, clientComManager);
        return forkJoinPool.invoke(requestExecutor);
    }

    private Response executeRequest(Request request, ClientComManager clientComManager) {
        if (request == null){
            return new Response(ResponseStatus.ERROR);
            // TODO
        }
        else if (request.getCommandObjectArgument() != null){
            return new Response(ResponseStatus.ERROR);
            // TODO
        }
        else if (request.getCommandName().equals("exit")){
            return new Response(ResponseStatus.CLIENT_EXIT);
        }
        else if (request.getCommandName().equals("sign_in")){
            UserInfo userInfo = request.getUserInfo();
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();

            int id = dataBaseHandler.authorize(username, password);
            if (id >= 0){
                return new Response(ResponseStatus.SUCCESS, "Авторизация прошла успешно");
            }
            else {
                return new Response(ResponseStatus.ERROR, "Неправильный логин или пароль");
            }
        }
        else if (request.getCommandName().equals("sign_up")){
            UserInfo userInfo = request.getUserInfo();
            String username = userInfo.getUsername();
            String password = userInfo.getPassword();

            return dataBaseHandler.addUser(username, password);
        }
        else{
            UserInfo userInfo = request.getUserInfo();
            id = dataBaseHandler.authorize(userInfo.getUsername(), userInfo.getPassword());

            if (id >= 0) {
                try {
                    return commandManager.execute(request.getCommandName(), request.getCommandStringArgument(), clientComManager, id);
                } catch (IOException e) {
                    return new Response(ResponseStatus.ERROR, "Сетевая ошибка");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return new Response(ResponseStatus.ERROR, "Невалидный ввод от клиента");
                }
            }
            else {
                return new Response(ResponseStatus.ERROR, "Авторизационная сессия больше не действительна");
            }
        }
    }

    private class RequestExecutor extends RecursiveTask<Response> {
        private Request request;
        private ClientComManager clientComManager;

        public RequestExecutor(Request request, ClientComManager clientComManager){
            this.request = request;
            this.clientComManager = clientComManager;
        }

        @Override
        protected Response compute() {
            return executeRequest(request, clientComManager);
        }
    }
}
