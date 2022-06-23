package server.commands;

import interaction.Response;
import server.io.ClientComManager;

import java.io.IOException;

public interface Command {
    Response execute(String arg, ClientComManager clientComManager, int id) throws IOException, ClassNotFoundException;
}
