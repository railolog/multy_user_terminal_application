package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class FilterGreaterThanSeaLevel implements Command{
    private CityCollectionManager collectionManager;

    public FilterGreaterThanSeaLevel(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.filterGreaterThanSeaLevel(arg);
    }

    @Override
    public String toString() {
        return "вывести элементы, значение поля metersAboveSeaLevel которых больше заданного";
    }
}
