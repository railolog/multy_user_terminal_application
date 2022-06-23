package server;

import com.google.gson.JsonParseException;
import interaction.Response;
import server.collection.CityCollectionManager;
import server.commands.*;
import server.io.ConsoleManager;
import server.io.DataBaseHandler;
import server.io.FileManager;
import server.utils.CommandManager;
import server.utils.RequestHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int PORT = 1337;
        final String CONFIG_PATH = "config_info.txt";
        Scanner configInfoSc = null;
        DataBaseHandler dataBaseHandler = null;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String jdbcURL = null;
        String userName = null;
        String passWord = null;

        try{
            configInfoSc = new Scanner(new File(CONFIG_PATH));
        } catch (FileNotFoundException e) {
            System.err.println("Не найден файл " + CONFIG_PATH + " с данными для входа");
            System.exit(-1);
        }

        try {
            jdbcURL = configInfoSc.nextLine().trim();
            userName = configInfoSc.nextLine().trim();
            passWord = configInfoSc.nextLine().trim();
        }
        catch (NoSuchElementException e){
            System.err.println("Недостаточно данных для подключения в файле.\nНеобходимые строки: URL, username, password.");
            System.exit(-1);
        }

        try {
            dataBaseHandler = new DataBaseHandler(jdbcURL, userName, passWord);
        } catch (SQLException e) {
            System.err.println("Не удалось подключиться к базе данных\n" + e.getMessage());
            System.exit(-1);
        }

        /*Response r;
        r = dataBaseHandler.authorize("railolog", "1a1dc91c907325c69271ddf0c944bc72");
        System.out.println(r.getResponseBody());
        r = dataBaseHandler.authorize("railolog", "1a1dc1c907325c69271ddf0c944bc72");
        System.out.println(r.getResponseBody());
        r = dataBaseHandler.authorize("railoog", "1a1dc91c907325c69271ddf0c944bc72");
        System.out.println(r.getResponseBody());*/

        CityCollectionManager collectionManager = new CityCollectionManager(dataBaseHandler);

        try {
            collectionManager.setCollection(dataBaseHandler.readCollection());
        }
        catch (SQLException e){
            e.printStackTrace();
            // TODO "Не удалось загрузить коллекцию
        }

        CommandManager commandManager = new CommandManager();

        commandManager.addCommand("add", new AddCommand(collectionManager));
        commandManager.addCommand("add_if_max", new AddIfMaxCommand(collectionManager));
        commandManager.addCommand("clear", new ClearCommand(collectionManager));
        commandManager.addCommand("filter_greater_than_meters_above_sea_level", new FilterGreaterThanSeaLevel(collectionManager));
        commandManager.addCommand("group_counting_by_coordinates", new GroupCountingCommand(collectionManager));
        commandManager.addCommand("info", new InfoCommand(collectionManager));
        commandManager.addCommand("min_by_creation_date", new MinByCreationDateCommand(collectionManager));
        commandManager.addCommand("remove_by_id", new RemoveByIdCommand(collectionManager));
        commandManager.addCommand("reorder", new ReorderCommand(collectionManager));
        commandManager.addCommand("show", new ShowCommand(collectionManager));
        commandManager.addCommand("shuffle", new ShuffleCommand(collectionManager));
        commandManager.addCommand("update", new UpdateCommand(collectionManager));

        Server server = new Server(PORT, collectionManager, dataBaseHandler, commandManager);

        server.run();
    }
}
