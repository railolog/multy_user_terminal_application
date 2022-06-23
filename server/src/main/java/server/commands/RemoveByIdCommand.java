package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class RemoveByIdCommand implements Command{
    private CityCollectionManager collectionManager;

    public RemoveByIdCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.removeById(arg, id);
    }

    @Override
    public String toString() {
        return "удалить элемент из коллекции по его id";
    }
}
