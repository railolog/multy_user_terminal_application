package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class ShuffleCommand implements Command{
    private CityCollectionManager collectionManager;

    public ShuffleCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.shuffle();
    }

    @Override
    public String toString() {
        return "перемешать элементы коллекции в случайном порядке";
    }
}
