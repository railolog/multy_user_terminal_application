package client.utils;

import client.io.ConsoleIOManager;
import interaction.Request;
import interaction.Response;
import interaction.ResponseStatus;
import interaction.UserInfo;

public class ResponseHandler {
    private UserInfo userInfo;

    public ResponseHandler(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    public Request handleResponse(Response response){
        if (response == null || response.getResponseStatus() == ResponseStatus.SUCCESS){
            try {
                assert response != null;
                ConsoleIOManager.println(response.getResponseBody());
            }
            catch (NullPointerException ignored){
            }

            CommandWrapper command = ConsoleIOManager.readCommand();
            return new Request(command.getCommand(), userInfo, command.getArgument());
        }
        else if (response.getResponseStatus() == ResponseStatus.SERVER_SHUTDOWN){
            ConsoleIOManager.println("Сервер прекратил свою работу");
            System.exit(0);
            return null;
        }
        else if (response.getResponseStatus() == ResponseStatus.READ_ELEM){
            return new Request("", userInfo, null, ConsoleIOManager.readCity());
        }
        else if (response.getResponseStatus() == ResponseStatus.EXECUTE_SCRIPT){
            String path = response.getResponseBody();

            if (path == null || path.length() == 0){
                ConsoleIOManager.printErr("Не введен путь к файлу");
                return handleResponse(null);
            }

            ConsoleIOManager.setFileInput(path);
            return handleResponse(null);
        }
        else if (response.getResponseStatus() == ResponseStatus.ERROR){
            if (response.getResponseBody() != null){
                ConsoleIOManager.printErr(response.getResponseBody());
            }
            else {
                ConsoleIOManager.printErr("непредвиденная ошибка");
            }

            CommandWrapper command = ConsoleIOManager.readCommand();
            return new Request(command.getCommand(), userInfo, command.getArgument());
        }
        else{
            return null;
        }
    }
}
