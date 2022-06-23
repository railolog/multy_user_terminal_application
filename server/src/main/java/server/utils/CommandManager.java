package server.utils;

import interaction.Response;
import interaction.ResponseStatus;
import server.commands.Command;
import server.io.ClientComManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final HashMap<String, Command> commandMap = new HashMap<>();

    public CommandManager(){
        addCommand("help", new HelpCommand());
        addCommand("exit", new ExitCommand());
        addCommand("execute_script", new ExecuteScriptCommand());
    }

    public class HelpCommand implements Command{
        @Override
        public Response execute(String arg, ClientComManager clientComManager, int id) {
            return new Response(ResponseStatus.SUCCESS, getHelp());
        }

        public String toString(){
            return "вывести справку по доступным командам";
        }
    }

    public class ExitCommand implements Command{
        @Override
        public Response execute(String arg, ClientComManager clientComManager, int id) {
            return new Response(ResponseStatus.SERVER_SHUTDOWN);
        }

        @Override
        public String toString() {
            return "завершить программу (без сохранения в файл)";
        }
    }

    public class ExecuteScriptCommand implements Command{
        @Override
        public Response execute(String arg, ClientComManager clientComManager, int id) {
            return new Response(ResponseStatus.EXECUTE_SCRIPT, arg);
        }

        @Override
        public String toString() {
            return "считать и исполнить скрипт из указанного файла";
        }
    }

    public void addCommand(String commandName, Command command){
        commandMap.put(commandName, command);
    }

    public Response execute(String commandName, String arg, ClientComManager clientComManager, int id) throws IOException, ClassNotFoundException {
        Command command = commandMap.get(commandName);
        if (command == null){
            return new Response(ResponseStatus.ERROR, "Введена несуществующа команда");
        }
        return command.execute(arg, clientComManager, id);
    }

    public String getHelp(){
        StringBuilder helpList = new StringBuilder();

        for(Map.Entry<String, Command> entry: commandMap.entrySet()){
            if (!entry.getKey().equals("exit"))
                helpList.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        helpList.deleteCharAt(helpList.length() - 1);
        return helpList.toString();
    }
}
