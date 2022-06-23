package client.utils;

public class CommandWrapper {
    private final String argument;
    private final String command;

    public CommandWrapper(String cmd){
        command = cmd;
        argument = null;
    }

    public CommandWrapper(String cmd, String arg){
        command = cmd;
        argument = arg;
    }

    public String getCommand(){
        return command;
    }

    public String getArgument(){
        return argument;
    }
}
