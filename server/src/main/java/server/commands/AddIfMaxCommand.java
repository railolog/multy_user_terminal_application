package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

import java.io.IOException;

public class AddIfMaxCommand implements Command{
    private CityCollectionManager collectionManager;

    public AddIfMaxCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) throws IOException, ClassNotFoundException {
        return collectionManager.addIfMax(clientComManager, id);
    }

    @Override
    public String toString() {
        return "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }
}
