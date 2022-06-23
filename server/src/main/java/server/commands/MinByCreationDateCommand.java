package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class MinByCreationDateCommand implements Command{
    private CityCollectionManager collectionManager;

    public MinByCreationDateCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.minByCreationDate();
    }

    @Override
    public String toString() {
        return "вывести любой объект из коллекции, значение поля creationDate которого является минимальным";
    }
}
