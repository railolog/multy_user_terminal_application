package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

import java.io.IOException;

public class UpdateCommand implements Command{
    private CityCollectionManager collectionManager;

    public UpdateCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) throws IOException, ClassNotFoundException {
        return collectionManager.update(arg, clientComManager, id);
    }

    @Override
    public String toString() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}
