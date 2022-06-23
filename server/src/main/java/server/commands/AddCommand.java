package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

import java.io.IOException;

public class AddCommand implements Command{
    private CityCollectionManager collectionManager;

    public AddCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) throws IOException, ClassNotFoundException {
        return collectionManager.addElement(clientComManager, id);
    }

    @Override
    public String toString() {
        return "добавить новый элемент в коллекцию";
    }
}
