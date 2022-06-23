package server.commands;

import interaction.Response;
import server.collection.CityCollectionManager;
import server.io.ClientComManager;

public class ShowCommand implements Command{
    private CityCollectionManager collectionManager;

    public ShowCommand(CityCollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(String arg, ClientComManager clientComManager, int id) {
        return collectionManager.printElements();
    }

    @Override
    public String toString(){
        return "вывести все элементы коллекции";
    }
}
