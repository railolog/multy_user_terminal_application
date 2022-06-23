package interaction;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final String commandStringArgument;
    private final UserInfo userInfo;
    private final Serializable commandObjectArgument;

    public Request(String commandName, UserInfo userInfo, String commandStringArgument, Serializable commandObjectArgument) {
        this.commandName = commandName;
        this.commandStringArgument = commandStringArgument;
        this.userInfo = userInfo;
        this.commandObjectArgument = commandObjectArgument;
    }

    public Request(String commandName, UserInfo userInfo, String commandStringArgument){
        this(commandName, userInfo, commandStringArgument, null);
    }

    public Request(String commandName, UserInfo userInfo) {
        this(commandName, userInfo, null, null);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getCommandName(){
        return commandName;
    }

    public String getCommandStringArgument() {
        return commandStringArgument;
    }

    public Serializable getCommandObjectArgument() {
        return commandObjectArgument;
    }
}
