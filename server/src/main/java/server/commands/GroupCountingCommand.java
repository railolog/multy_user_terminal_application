package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class GroupCountingCommand implements Command{
    private CityCollectionManager collectionManager;

    public GroupCountingCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.groupCountingByCoordinates();
    }

    @Override
    public String toString() {
        return "сгруппировать элементы коллекции по значению поля coordinates";
    }
}
