package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class InfoCommand implements Command{
    private CityCollectionManager collectionManager;

    public InfoCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.showCollectionInfo();
    }

    public String toString(){
        return "вывести информацию о коллекции";
    }
}
