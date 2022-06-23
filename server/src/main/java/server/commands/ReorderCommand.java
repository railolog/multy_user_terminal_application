package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class ReorderCommand implements Command{
    private CityCollectionManager collectionManager;

    public ReorderCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.reorder();
    }

    @Override
    public String toString() {
        return "отсортировать коллекцию в порядке, обратном нынешнему";
    }
}
