package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class ClearCommand implements Command{
    private CityCollectionManager collectionManager;

    public ClearCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.clearCollection(id);
    }

    @Override
    public String toString() {
        return "очистить коллекцию";
    }
}
